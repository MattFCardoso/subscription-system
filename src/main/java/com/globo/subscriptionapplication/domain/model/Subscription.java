package com.globo.subscriptionapplication.domain.model;

import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;

import java.time.LocalDate;
import java.util.UUID;

public class Subscription {

    private UUID subscriptionId;
    private User userId;
    private PlanEnum planEnum;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private SubscriptionStatusEnum status;
    private int failedAttempts;

}
