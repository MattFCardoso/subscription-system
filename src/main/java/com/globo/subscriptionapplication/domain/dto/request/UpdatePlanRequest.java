package com.globo.subscriptionapplication.domain.dto.request;

import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePlanRequest {

    @NotNull(message = "New plan is required")
    private PlanEnum newPlan;
}