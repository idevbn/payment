package com.ead.payment.services;

import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;

public interface PaymentStripeService {

    PaymentModel processStripePayment(final PaymentModel paymentModel,
                                      final CreditCardModel creditCardModel);

}
