package com.globo.subscriptionapplication.controller;

import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.dto.response.SubscriptionResponse;
import com.globo.subscriptionapplication.service.impl.SubscriptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Nested
@DisplayName("Create Subscription")
class CreateSubscriptionTests {

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    @Test
    @DisplayName("Should return 201 when subscription is created successfully")
    void shouldReturn201WhenSubscriptionCreatedSuccessfully() {
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder()
                .email("user@example.com")
                .plan(PlanEnum.BASICO)
                .build();
        SubscriptionResponse response = SubscriptionResponse.builder()
                .subscriptionId(UUID.randomUUID())
                .plan(PlanEnum.BASICO)
                .userId(UUID.randomUUID())
                .build();

        when(subscriptionService.createSubscription(request)).thenReturn(response);

        ResponseEntity<SubscriptionResponse> result = subscriptionController.createSubscription(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(subscriptionService, times(1)).createSubscription(request);
    }

    @Test
    @DisplayName("Should return 200 when subscription is found")
    void shouldReturn200WhenSubscriptionIsFound() {
        UUID subscriptionId = UUID.randomUUID();
        SubscriptionResponse response = SubscriptionResponse.builder()
                .subscriptionId(subscriptionId)
                .plan(PlanEnum.BASICO)
                .userId(UUID.randomUUID())
                .build();

        when(subscriptionService.getSubscriptionById(subscriptionId)).thenReturn(response);

        ResponseEntity<SubscriptionResponse> result = subscriptionController.getSubscription(subscriptionId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(subscriptionService, times(1)).getSubscriptionById(subscriptionId);
    }

    @Test
    @DisplayName("Should return 200 when active subscription is found for user")
    void shouldReturn200WhenActiveSubscriptionIsFoundForUser() {
        UUID userId = UUID.randomUUID();
        SubscriptionResponse response = SubscriptionResponse.builder()
                .subscriptionId(UUID.randomUUID())
                .plan(PlanEnum.BASICO)
                .userId(userId)
                .build();

        when(subscriptionService.getActiveSubscriptionByUserId(userId)).thenReturn(response);

        ResponseEntity<SubscriptionResponse> result = subscriptionController.getSubscriptionsByUser(userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(subscriptionService, times(1)).getActiveSubscriptionByUserId(userId);
    }
}