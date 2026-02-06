package com.globo.subscriptionapplication.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsRequest {

    // Para Cartão de Crédito
    @NotBlank(message = "Card number cannot be blank")
    @Pattern(regexp = "\\d{16}", message = "Card number must have 16 digits")
    private String cardNumber;

    @NotBlank(message = "Card holder name cannot be blank")
    @Size(min = 3, max = 100, message = "Card holder name must be between 3 and 100 characters")
    private String cardHolderName;

    @NotBlank(message = "Expiration date cannot be blank")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiration date must be in format MM/YY")
    private String expirationDate;

    @NotBlank(message = "CVV cannot be blank")
    @Pattern(regexp = "\\d{3,4}", message = "CVV must have 3 or 4 digits")
    private String cvv;

}
