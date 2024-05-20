package com.ead.payment.services.impl;

import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.services.PaymentStripeService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentStripeServiceImpl implements PaymentStripeService {

    @Value(value = "${ead.stripe.secretKey}")
    private String secretKeyStripe;

    @Override
    public PaymentModel processStripePayment(
            final PaymentModel paymentModel,
            final CreditCardModel creditCardModel
    ) {
        Stripe.apiKey = this.secretKeyStripe;
        String paymentIntentId = null;

        try {
            final List<Object> paymentMethodTypes = new ArrayList<>();
            paymentMethodTypes.add("card");

            final Map<String, Object> paramsPaymentIntent = new HashMap<>();
            paramsPaymentIntent.put("amount", paymentModel.getValuePaid().
                    multiply(new BigDecimal("100")).longValue());
            paramsPaymentIntent.put("currency", "brl");
            paramsPaymentIntent.put("payment_method_types", paymentMethodTypes);

            final PaymentIntent paymentIntent = PaymentIntent.create(paramsPaymentIntent);
            paymentIntentId = paymentIntent.getId();

            final Map<String, Object> paramsPaymentMethod = new HashMap<>();
            paramsPaymentMethod.put("type", "card");

            final Map<String, Object> card = new HashMap<>();
            card.put("number", creditCardModel.getCreditCardNumber()
                    .replaceAll(" ", ""));
            card.put("exp_month", creditCardModel.getExpirationDate().split("/")[0]);
            card.put("exp_year", creditCardModel.getExpirationDate().split("/")[1]);
            card.put("cvc", creditCardModel.getCvvCode());

            paramsPaymentMethod.put("card", card);
            final PaymentMethod paymentMethod = PaymentMethod.create(paramsPaymentMethod);

            final Map<String, Object> paramsPaymentConfirm = new HashMap<>();
            paramsPaymentConfirm.put("payment_method", paymentMethod.getId());

            final PaymentIntent confirmPaymentIntent = paymentIntent.confirm(paramsPaymentConfirm);
        } catch (final Exception ex) {

        }

        return paymentModel;
    }

}
