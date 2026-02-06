package com.globo.subscriptionapplication.dto.request;

import com.globo.subscriptionapplication.domain.enums.PaymentMethodEnum;
import com.globo.subscriptionapplication.domain.enums.PlanEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Plan cannot be null")
    private PlanEnum plan;

    @NotNull(message = "Payment method cannot be blank")
    private PaymentMethodEnum paymentMethod;

    @Valid
    @NotNull(message = "Payment details cannot be null")
    private PaymentDetailsRequest paymentDetails;
}
