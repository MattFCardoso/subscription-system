package com.globo.subscriptionapplication.service.interfaces;

import com.globo.subscriptionapplication.domain.model.Payment;
import com.globo.subscriptionapplication.domain.model.Subscription;

public interface PaymentService {
    Payment processPayment(Subscription subscription, int attemptNumber);
}
