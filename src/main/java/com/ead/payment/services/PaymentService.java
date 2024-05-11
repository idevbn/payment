package com.ead.payment.services;

import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;

public interface PaymentService {

    PaymentModel requestPaymentStatus(
            final PaymentRequestDTO paymentRequestDTO,
            final UserModel userModel
    );

}
