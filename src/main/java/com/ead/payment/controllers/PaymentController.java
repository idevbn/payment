package com.ead.payment.controllers;

import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.enums.PaymentControl;
import com.ead.payment.models.PaymentModel;
import com.ead.payment.models.UserModel;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.UserService;
import com.ead.payment.specifications.SpecificationTemplate;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(final PaymentService paymentService,
                             final UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("users/{userId}/payments")
    public ResponseEntity<Object> requestPayment(
            @PathVariable(value = "userId") final UUID userId,
            @RequestBody @Valid final PaymentRequestDTO paymentRequestDTO
            ) {
        final Optional<UserModel> userModelOptional =  this.userService.findById(userId);

        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        final Optional<PaymentModel> paymentModelOptional = this.paymentService
                .findLastPaymentByUser(userModelOptional.get());

        if (paymentModelOptional.isPresent()) {

            if (paymentModelOptional.get().getPaymentControl().equals(PaymentControl.REQUESTED)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment already requested.");
            }

            if (paymentModelOptional.get().getPaymentControl().equals(PaymentControl.EFFECTED) &&
                    paymentModelOptional.get().getPaymentExpirationDate()
                            .isAfter(LocalDateTime.now(ZoneId.of("UTC")))) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment already made.");
            }
        }

        final PaymentModel paymentModel = this.paymentService
                .requestPaymentStatus(paymentRequestDTO, userModelOptional.get());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentModel);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("users/{userId}/payments")
    public ResponseEntity<Page<PaymentModel>> getAllPayments(
            @PathVariable(value = "userId") final UUID userId,
            final SpecificationTemplate.PaymentSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "paymentId", direction = Sort.Direction.DESC)
            final Pageable pageable
            ) {
        final Page<PaymentModel> paymentModelPage = this.paymentService
                .findAllByUser(SpecificationTemplate.paymentUserId(userId).and(spec), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(paymentModelPage);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("users/{userId}/payments/{paymentId}")
    public ResponseEntity<Object> getOnePayment(
            @PathVariable(value = "userId") final UUID userId,
            @PathVariable(value = "paymentId") final UUID paymentId
    ) {
        final Optional<PaymentModel> paymentModelOptional = this.paymentService
                .findPaymentByUser(userId, paymentId);

        if (paymentModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment nof found for this user.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(paymentModelOptional.get());
    }

}
