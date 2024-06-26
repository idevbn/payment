package com.ead.payment.models;

import com.ead.payment.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID userId;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false)
    private String userStatus;

    @Column(nullable = false)
    private String userType;

    @Column(length = 20)
    private String cpf;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column
    private LocalDateTime paymentExpirationDate;

    @Column
    private LocalDateTime firstPaymentDate;

    @Column
    private LocalDateTime lastPaymentDate;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<PaymentModel> payments;

}
