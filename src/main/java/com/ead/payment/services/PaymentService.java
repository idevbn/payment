package com.ead.payment.services;

import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;

import java.util.Optional;

public interface PaymentService {

    PaymentModel requestPaymentStatus(
            final PaymentRequestDTO paymentRequestDTO,
            final UserModel userModel
    );

    Optional<PaymentModel> findLastPaymentByUser(final UserModel userModel);

}
