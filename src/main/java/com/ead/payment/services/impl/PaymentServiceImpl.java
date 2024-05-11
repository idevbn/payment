package com.ead.payment.services.impl;

import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.enums.PaymentControl;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import com.ead.payment.repositories.CreditCardRepository;
import com.ead.payment.repositories.PaymentRepository;
import com.ead.payment.services.PaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final CreditCardRepository creditCardRepository;
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(final CreditCardRepository creditCardRepository,
                              final PaymentRepository paymentRepository) {
        this.creditCardRepository = creditCardRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public PaymentModel requestPaymentStatus(
            final PaymentRequestDTO paymentRequestDTO,
            final UserModel userModel
    ) {
        final CreditCardModel creditCardModel = new CreditCardModel();

        BeanUtils.copyProperties(paymentRequestDTO, creditCardModel);
        creditCardModel.setUser(userModel);

        this.creditCardRepository.save(creditCardModel);

        final PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPaymentControl(PaymentControl.REQUESTED);
        paymentModel.setPaymentRequestDate(LocalDateTime.now(ZoneId.of("UTC")));
        paymentModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC")).plusDays(30));
        paymentModel.setLastDigitsCreditCard(paymentRequestDTO.getCreditCardNumber().substring(
                paymentRequestDTO.getCreditCardNumber().length() - 4
                )
        );
        paymentModel.setValuePaid(paymentRequestDTO.getValuePaid());

        paymentModel.setUser(userModel);

        this.paymentRepository.save(paymentModel);

        // send request to queue
        return paymentModel;
    }

}
