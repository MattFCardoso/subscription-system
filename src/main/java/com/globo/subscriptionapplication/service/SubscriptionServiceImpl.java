package com.globo.subscriptionapplication.service;

import com.globo.subscriptionapplication.domain.enums.PaymentMethodEnum;
import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.model.User;
import com.globo.subscriptionapplication.domain.repository.SubscriptionRepository;
import com.globo.subscriptionapplication.domain.repository.UserRepository;
import com.globo.subscriptionapplication.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.dto.request.PaymentDetailsRequest;
import com.globo.subscriptionapplication.dto.response.SubscriptionResponse;
import com.globo.subscriptionapplication.exception.SubscriptionException;
import com.globo.subscriptionapplication.service.impl.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        log.info("Creating subscription for user: {} with plan: {}", request.getEmail(), request.getPlan());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        // verify active subscription
        if (subscriptionRepository.existsByUserIdAndStatus(user, SubscriptionStatusEnum.ATIVA)) {
            throw new RuntimeException("User already has an active subscription");
        }


        // Processar pagamento primeiro
        boolean paymentSuccessful = processPayment(request.getPaymentDetails(),
                request.getPlan(),
                user,
                request.getPaymentMethod());

        if (!paymentSuccessful) {
            throw new SubscriptionException("Payment failed. Please check your payment details.");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate expirationDate = startDate.plusMonths(1);

        Subscription subscription = Subscription.builder()
                .userId(user)
                .plan(request.getPlan())
                .startDate(startDate)
                .expirationDate(expirationDate)
                .status(SubscriptionStatusEnum.ATIVA)
                .renewalAttempts(0)
                .createdAt(LocalDateTime.now())
                .build();

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription created successfully: {}", subscription.getSubscriptionId());

        return mapToResponse(subscription);
    }

    private boolean processPayment(PaymentDetailsRequest paymentDetails,
                                   PlanEnum plan,
                                   User user,
                                   PaymentMethodEnum paymentMethod) {
        // Aqui você integraria com um gateway de pagamento
        // Ex: Stripe, PagSeguro, Mercado Pago
        log.info("Processing payment for user: {} with method: {}", user.getEmail(), paymentMethod);

        // Simulação (em produção, chamaria API externa)
        return true; // ou false se falhar
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
    public Subscription suspendSubscription(UUID subscriptionId) {
        log.info("Suspending subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        subscription.suspendSubscription();
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription suspended successfully: {}", subscriptionId);

        return subscription;
    }

    @Transactional
    public void renewSubscription(UUID subscriptionId, LocalDate newExpirationDate) {
        log.info("Renewing subscription: {} until: {}", subscriptionId, newExpirationDate);

        Subscription subscription = subscriptionRepository.findByIdWithLock(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        subscription.renewSubscription(newExpirationDate);
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription renewed successfully: {}", subscriptionId);

    }

    @Transactional
    public Subscription updatePlan(UUID subscriptionId, PlanEnum newPlan) {
        log.info("Updating plan for subscription: {} to: {}", subscriptionId, newPlan);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatusEnum.ATIVA) {
            throw new RuntimeException("Only active subscriptions can have their plan changed");
        }

        subscription.setPlan(newPlan);
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        log.info("Plan updated successfully for subscription: {}", subscriptionId);

        return subscription;
    }

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> findExpiredSubscriptions(LocalDate date) {
        log.debug("Finding expired subscriptions for date: {}", date);

        Subscription subscription = subscriptionRepository.findExpiredSubscriptions(date, SubscriptionStatusEnum.ATIVA)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No expired subscriptions found for date: " + date));
        return Collections.singletonList(mapToResponse(subscription));
    }

    @Transactional
    public void incrementRenewalAttempts(UUID subscriptionId) {
        log.info("Incrementing renewal attempts for subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        subscription.incrementRenewalAttempts();
        subscription.setUpdatedAt(LocalDateTime.now());

        // Se excedeu as tentativas, suspende a assinatura
        if (subscription.attemptsExceeded()) {
            subscription.suspendSubscription();
            log.warn("Subscription suspended due to exceeded renewal attempts: {}", subscriptionId);
        }

        subscriptionRepository.save(subscription);
    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .userId(subscription.getUserId().getUserId())
                .plan(subscription.getPlan())
                .startDate(subscription.getStartDate())
                .expirationDate(subscription.getExpirationDate())
                .status(subscription.getStatus())
                .renewalAttempts(subscription.getRenewalAttempts())
                .build();
    }
}
