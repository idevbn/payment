package com.ead.payment.publishers;

import com.ead.payment.dtos.PaymentEventDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ead.broker.exchange.paymentEventExchange}")
    private String exchangePaymentEvent;

    public PaymentEventPublisher(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPaymentEvent(final PaymentEventDTO paymentEventDTO) {
        this.rabbitTemplate.convertAndSend(this.exchangePaymentEvent, "", paymentEventDTO);
    }

}
