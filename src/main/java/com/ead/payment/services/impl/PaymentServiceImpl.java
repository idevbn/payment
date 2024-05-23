package com.ead.payment.services.impl;

import com.ead.payment.dtos.PaymentCommandDTO;
import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.enums.PaymentControl;
import com.ead.payment.enums.PaymentStatus;
import com.ead.payment.models.CreditCardModel;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import com.ead.payment.publishers.PaymentCommandPublisher;
import com.ead.payment.repositories.CreditCardRepository;
import com.ead.payment.repositories.PaymentRepository;
import com.ead.payment.repositories.UserRepository;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.PaymentStripeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {

    private final CreditCardRepository creditCardRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCommandPublisher paymentCommandPublisher;
    private final UserRepository userRepository;
    private final PaymentStripeService paymentStripeService;

    public PaymentServiceImpl(final CreditCardRepository creditCardRepository,
                              final PaymentRepository paymentRepository,
                              final PaymentCommandPublisher paymentCommandPublisher,
                              final UserRepository userRepository,
                              final PaymentStripeService paymentStripeService) {
        this.creditCardRepository = creditCardRepository;
        this.paymentRepository = paymentRepository;
        this.paymentCommandPublisher = paymentCommandPublisher;
        this.userRepository = userRepository;
        this.paymentStripeService = paymentStripeService;
    }

    @Override
    @Transactional
    public PaymentModel requestPaymentStatus(
            final PaymentRequestDTO paymentRequestDTO,
            final UserModel userModel
    ) {
        CreditCardModel creditCardModel = new CreditCardModel();
        final Optional<CreditCardModel> creditCardModelOptional = this.creditCardRepository
                .findByUser(userModel);

        if (creditCardModelOptional.isPresent()) {
            creditCardModel = creditCardModelOptional.get();
        }

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

        try {
            final PaymentCommandDTO paymentCommandDTO = new PaymentCommandDTO();
            paymentCommandDTO.setUserId(userModel.getUserId());
            paymentCommandDTO.setPaymentId(paymentModel.getPaymentId());
            paymentCommandDTO.setCardId(creditCardModel.getCardId());

            this.paymentCommandPublisher.publishPaymentCommand(paymentCommandDTO);
        } catch (final Exception e) {
            log.warn("Error sending payment command!");
        }

        return paymentModel;
    }

    @Override
    public Optional<PaymentModel> findLastPaymentByUser(final UserModel userModel) {
        return this.paymentRepository.findTopByUserOrderByPaymentRequestDateDesc(userModel);
    }

    @Override
    public Page<PaymentModel> findAllByUser(
            final Specification<PaymentModel> spec,
            final Pageable pageable
    ) {
        return this.paymentRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<PaymentModel> findPaymentByUser(final UUID userId, final UUID paymentId) {
        return this.paymentRepository.findPaymentByUser(userId, paymentId);
    }

    @Override
    @Transactional
    public void makePayment(final PaymentCommandDTO paymentCommandDTO) {
        var paymentModel = this.paymentRepository
                .findById(paymentCommandDTO.getPaymentId()).get();

        var userModel = this.userRepository
                .findById(paymentCommandDTO.getUserId()).get();

        var creditCardModel = this.creditCardRepository
                .findById(paymentCommandDTO.getCardId()).get();

        paymentModel = this.paymentStripeService
                .processStripePayment(paymentModel, creditCardModel);

        if (paymentModel.getPaymentControl().equals(PaymentControl.EFFECTED)) {
            userModel.setPaymentStatus(PaymentStatus.PAYING);
            userModel.setLastPaymentDate(LocalDateTime.now(ZoneId.of("UTC")));
            userModel.setPaymentExpirationDate(LocalDateTime.now(ZoneId.of("UTC"))
                    .plusDays(30));

            if (userModel.getFirstPaymentDate() == null) {
                userModel.setFirstPaymentDate(LocalDateTime.now(ZoneId.of("UTC")));
            }
        } else {
            userModel.setPaymentStatus(PaymentStatus.DEBTOR);
        }

        this.userRepository.save(userModel);
    }

}
