package com.globo.subscriptionapplication.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import com.globo.subscriptionapplication.domain.enums.SubscriptionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private UUID subscriptionId;
    private UUID userId;
    private PlanEnum plan;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String expirationDate;
    private SubscriptionStatusEnum status;
    private Integer renewalAttempts;
}