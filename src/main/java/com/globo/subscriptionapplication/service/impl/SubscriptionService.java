package com.globo.subscriptionapplication.service.impl;

import com.globo.subscriptionapplication.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.dto.response.SubscriptionResponse;

import java.time.LocalDate;
import java.util.UUID;

public interface SubscriptionService {

    SubscriptionResponse createSubscription(CreateSubscriptionRequest request);
    void cancelSubscription(UUID subscriptionId);
    void renewSubscription(UUID subscriptionId, LocalDate newExpirationDate);
    SubscriptionResponse getSubscriptionById(UUID subscriptionId);
    SubscriptionResponse getActiveSubscriptionByUserId(UUID userId);
}
