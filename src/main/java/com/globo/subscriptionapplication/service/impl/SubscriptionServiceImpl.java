package com.globo.subscriptionapplication.service.impl;

import com.globo.subscriptionapplication.domain.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.domain.dto.request.UpdatePlanRequest;
import com.globo.subscriptionapplication.domain.dto.response.SubscriptionResponse;
import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.model.User;
import com.globo.subscriptionapplication.domain.repository.SubscriptionRepository;
import com.globo.subscriptionapplication.domain.repository.UserRepository;
import com.globo.subscriptionapplication.events.MessageProducer;
import com.globo.subscriptionapplication.exception.SubscriptionException;
import com.globo.subscriptionapplication.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MessageProducer messageProducer;

    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        log.info("Creating subscription for user: {} with plan: {}", request.getEmail(), request.getPlan());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        if (subscriptionRepository.existsByUserAndStatus(user, SubscriptionStatusEnum.ATIVA)) {
            throw new RuntimeException("User already has an active subscription");
        }

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(request.getPlan())
                .status(SubscriptionStatusEnum.PAGAMENTO_PENDENTE)
                .renewalAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription created successfully: {}", subscription.getSubscriptionId());

        messageProducer.processSubscriptionPayment(subscription);
        return mapToResponse(subscription);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "subscriptions", key = "#subscriptionId")
    public SubscriptionResponse getSubscriptionById(UUID subscriptionId) {
        log.debug("Finding subscription by ID: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException("Subscription not found: " + subscriptionId));
        return mapToResponse(subscription);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "activeSubscriptions", key = "#userId")
    public SubscriptionResponse getActiveSubscriptionByUserId(UUID userId) {
        log.debug("Finding active subscription for user: {}", userId);

        Subscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatusEnum.ATIVA)
                .orElseThrow(() -> new SubscriptionException("No active subscription found for user: " + userId));
        return mapToResponse(subscription);
    }

    @Transactional
    @CacheEvict(value = "subscriptions", key = "#subscriptionId")
    public void cancelSubscription(UUID subscriptionId) {
        log.info("Cancelling subscription with ID: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException("Assinatura não encontrada com ID: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatusEnum.ATIVA) {
            throw new SubscriptionException("Apenas assinaturas ativas podem ser canceladas");
        }

        subscription.cancelSubscription();
        subscriptionRepository.save(subscription);

        log.info("Subscription cancelled successfully. User can use until: {}", subscription.getExpirationDate());
    }


    @Transactional
    public void renewSubscription(UUID subscriptionId, LocalDate newExpirationDate) {
        log.info("Renewing subscription: {} until: {}", subscriptionId, newExpirationDate);

        Subscription subscription = subscriptionRepository.findByIdWithLock(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        subscription.renewSubscription();
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription renewed successfully: {}", subscriptionId);

    }

    @Transactional
    @CacheEvict(value = {"subscriptions", "activeSubscriptions"}, key = "#subscriptionId")
    public SubscriptionResponse updateSubscriptionPlan(UUID subscriptionId, UpdatePlanRequest request) {
        log.info("Updating plan for subscription: {} to: {}", subscriptionId, request.getNewPlan());

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new SubscriptionException("Subscription not found: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatusEnum.ATIVA) {
            throw new SubscriptionException("Only active subscriptions can have their plan changed");
        }

        PlanEnum newPlan = request.getNewPlan();

        if (subscription.getPlan() == newPlan) {
            throw new SubscriptionException("New plan must be different from current plan");
        }

        subscription.setPlan(newPlan);
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        log.info("Plan updated successfully for subscription: {}", subscriptionId);

        return mapToResponse(subscription);
    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .userId(subscription.getUser().getUserId())
                .plan(subscription.getPlan())
                .startDate(subscription.getStartDate() != null ? subscription.getStartDate().toString() : null)
                .expirationDate(subscription.getExpirationDate() != null ? subscription.getExpirationDate().toString() : null)
                .status(subscription.getStatus())
                .renewalAttempts(subscription.getRenewalAttempts())
                .build();
    }
}
