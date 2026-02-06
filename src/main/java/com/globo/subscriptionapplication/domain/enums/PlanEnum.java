package com.globo.subscriptionapplication.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum PlanEnum {

    BASICO("Básico", new BigDecimal("19.90")),
    PREMIUM("Premium", new BigDecimal("39.90")),
    FAMILIA("Familia", new BigDecimal("59.90"));

    private final String description;
    private final BigDecimal price;
}
