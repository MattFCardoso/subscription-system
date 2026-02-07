package com.globo.subscriptionapplication.domain.enums;

import java.util.Random;

public enum PaymentStatusEnum {
    PENDING,
    SUCCESS,
    FAILED;

    private static final Random PRNG = new Random();

    public static PaymentStatusEnum randomPaymentStatus()  {
        PaymentStatusEnum[] status = values();
        return status[PRNG.nextInt(status.length)];
    }
}
