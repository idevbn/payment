package com.ead.payment.services;

import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    PaymentModel requestPaymentStatus(
            final PaymentRequestDTO paymentRequestDTO,
            final UserModel userModel
    );

    Optional<PaymentModel> findLastPaymentByUser(final UserModel userModel);

    Page<PaymentModel> findAllByUser(final Specification<PaymentModel> spec, final Pageable pageable);

    Optional<PaymentModel> findPaymentByUser(final UUID userId, final UUID paymentId);

}
