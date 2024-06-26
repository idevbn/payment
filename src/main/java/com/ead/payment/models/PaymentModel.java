package com.ead.payment.models;

import com.ead.payment.dtos.PaymentEventDTO;
import com.ead.payment.enums.PaymentControl;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_payments")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID paymentId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentControl paymentControl;

    @Column(nullable = false)
    private LocalDateTime paymentRequestDate;

    @Column
    private LocalDateTime paymentCompletionDate;

    @Column(nullable = false)
    private LocalDateTime paymentExpirationDate;

    @Column(nullable = false, length = 4)
    private String lastDigitsCreditCard;

    @Column(nullable = false)
    private BigDecimal valuePaid;

    @Column(length = 150)
    private String paymentMessage;

    @Column
    private boolean recurrence;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserModel user;

    public PaymentEventDTO convertToPaymentEventDTO() {
        final PaymentEventDTO paymentEventDTO = new PaymentEventDTO();

        BeanUtils.copyProperties(this, paymentEventDTO);
        paymentEventDTO.setPaymentControl(this.getPaymentControl().toString());
        paymentEventDTO.setUserId(this.getUser().getUserId());

        return paymentEventDTO;
    }

}
