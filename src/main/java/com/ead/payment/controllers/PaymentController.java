package com.ead.payment.controllers;

import com.ead.payment.dtos.PaymentRequestDTO;
import com.ead.payment.models.UserModel;
import com.ead.payment.services.PaymentService;
import com.ead.payment.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

        this.paymentService.requestPaymentStatus(paymentRequestDTO, userModelOptional.get());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("");
    }

}
