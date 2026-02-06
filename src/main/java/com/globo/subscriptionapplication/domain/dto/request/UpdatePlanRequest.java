package com.globo.subscriptionapplication.domain.dto.request;

import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanRequest {

    @NotNull(message = "Plan cannot be null")
    private PlanEnum plan;
}
