package com.globo.subscriptionapplication.domain.enums;

import java.util.Random;

public enum SubscriptionStatusEnum {
    ATIVA,
    CANCELADA,
    PAGAMENTO_PENDENTE,
    FALHA_PAGAMENTO,
    SUSPENSA,
    EXPIRADA;

    private static final Random PRNG = new Random();

    public static SubscriptionStatusEnum randomSubscriptionStatus()  {
        SubscriptionStatusEnum[] status = values();
        return status[PRNG.nextInt(status.length)];
    }
}