package com.globo.subscriptionapplication.service;

import com.globo.subscriptionapplication.domain.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.domain.dto.request.UpdatePlanRequest;
import com.globo.subscriptionapplication.domain.dto.response.SubscriptionResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface SubscriptionService {

    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    void cancelSubscription(UUID subscriptionId);
    void renewSubscription(UUID subscriptionId, LocalDate newExpirationDate);
    SubscriptionResponse getSubscriptionById(UUID subscriptionId);
    SubscriptionResponse getActiveSubscriptionByUserId(UUID userId);
    SubscriptionResponse updateSubscriptionPlan(UUID subscriptionId, UpdatePlanRequest request);
}
