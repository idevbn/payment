package com.ead.payment.consumers;

import com.ead.payment.dtos.PaymentCommandDTO;
import com.ead.payment.services.PaymentService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    private final PaymentService paymentService;

    public PaymentConsumer(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.paymentCommandQueue.name}", durable = "true"),
            exchange = @Exchange(
                    value = "${ead.broker.exchange.paymentCommandExchange}",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"
            ),
            key = "${ead.broker.key.paymentCommandKey}")
    )
    public void listenPaymentCommand(@Payload final PaymentCommandDTO paymentCommandDTO) {
        System.out.println("PaymentId - " + paymentCommandDTO.getPaymentId());
        System.out.println("UserId - " + paymentCommandDTO.getUserId());
    }

}
