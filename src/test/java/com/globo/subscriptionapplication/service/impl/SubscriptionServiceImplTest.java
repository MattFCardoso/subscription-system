package com.globo.subscriptionapplication.service.impl;

import com.globo.subscriptionapplication.domain.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.domain.dto.request.UpdatePlanRequest;
import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.model.User;
import com.globo.subscriptionapplication.domain.repository.SubscriptionRepository;
import com.globo.subscriptionapplication.domain.repository.UserRepository;
import com.globo.subscriptionapplication.events.MessageProducer;
import com.globo.subscriptionapplication.exception.SubscriptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSubscriptionShouldThrowExceptionWhenUserNotFound() {
        CreateSubscriptionRequest request = new CreateSubscriptionRequest("nonexistent@example.com", PlanEnum.BASICO);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> subscriptionService.createSubscription(request));
        assertEquals("User not found: nonexistent@example.com", exception.getMessage());
    }

    @Test
    void createSubscriptionShouldThrowExceptionWhenUserHasActiveSubscription() {
        User user = new User();
        user.setEmail("user@example.com");
        CreateSubscriptionRequest request = new CreateSubscriptionRequest(user.getEmail(), PlanEnum.BASICO);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(subscriptionRepository.existsByUserAndStatus(user, SubscriptionStatusEnum.ATIVA)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> subscriptionService.createSubscription(request));
        assertEquals("User already has an active subscription", exception.getMessage());
    }

    @Test
    void getSubscriptionByIdShouldThrowExceptionWhenSubscriptionNotFound() {
        UUID subscriptionId = UUID.randomUUID();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        SubscriptionException exception = assertThrows(SubscriptionException.class, () -> subscriptionService.getSubscriptionById(subscriptionId));
        assertEquals("Subscription not found: " + subscriptionId, exception.getMessage());
    }

    @Test
    void cancelSubscriptionShouldThrowExceptionWhenSubscriptionNotFound() {
        UUID subscriptionId = UUID.randomUUID();

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        SubscriptionException exception = assertThrows(SubscriptionException.class, () -> subscriptionService.cancelSubscription(subscriptionId));
        assertEquals("Assinatura não encontrada com ID: " + subscriptionId, exception.getMessage());
    }

    @Test
    void cancelSubscriptionShouldThrowExceptionWhenSubscriptionIsNotActive() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatusEnum.PAGAMENTO_PENDENTE);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        SubscriptionException exception = assertThrows(SubscriptionException.class, () -> subscriptionService.cancelSubscription(subscriptionId));
        assertEquals("Apenas assinaturas ativas podem ser canceladas", exception.getMessage());
    }

    @Test
    void updateSubscriptionPlanShouldThrowExceptionWhenSubscriptionNotFound() {
        UUID subscriptionId = UUID.randomUUID();
        UpdatePlanRequest request = new UpdatePlanRequest(PlanEnum.PREMIUM);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.empty());

        SubscriptionException exception = assertThrows(SubscriptionException.class, () -> subscriptionService.updateSubscriptionPlan(subscriptionId, request));
        assertEquals("Subscription not found: " + subscriptionId, exception.getMessage());
    }

    @Test
    void updateSubscriptionPlanShouldThrowExceptionWhenSubscriptionIsNotActive() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatusEnum.PAGAMENTO_PENDENTE);
        UpdatePlanRequest request = new UpdatePlanRequest(PlanEnum.PREMIUM);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        SubscriptionException exception = assertThrows(SubscriptionException.class, () -> subscriptionService.updateSubscriptionPlan(subscriptionId, request));
        assertEquals("Only active subscriptions can have their plan changed", exception.getMessage());
    }

    @Test
    void updateSubscriptionPlanShouldThrowExceptionWhenNewPlanIsSameAsCurrentPlan() {
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatusEnum.ATIVA);
        subscription.setPlan(PlanEnum.BASICO);
        UpdatePlanRequest request = new UpdatePlanRequest(PlanEnum.BASICO);

        when(subscriptionRepository.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        SubscriptionException exception = assertThrows(SubscriptionException.class, () -> subscriptionService.updateSubscriptionPlan(subscriptionId, request));
        assertEquals("New plan must be different from current plan", exception.getMessage());
    }
}