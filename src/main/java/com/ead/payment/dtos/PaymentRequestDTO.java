package com.ead.payment.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NotNull
    @Digits(integer = 5, fraction = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal valuePaid;

    @NotBlank
    private String cardHolderFullName;

    @CPF
    @NotBlank
    private String cardHolderCpf;

    @NotBlank
    @Size(min = 16, max = 20)
    private String creditCardNumber;

    @NotBlank
    @Size(min = 4, max = 10)
    private String expirationDate;

    @NotBlank
    @Size(min = 3, max = 3)
    private String cvvCode;

}
