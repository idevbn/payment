package com.ead.payment.consumers;

import com.ead.payment.dtos.UserEventDTO;
import com.ead.payment.enums.ActionType;
import com.ead.payment.enums.PaymentStatus;
import com.ead.payment.models.UserModel;
import com.ead.payment.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    private final UserService userService;

    public UserConsumer(final UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
                    exchange = @Exchange(
                            value = "${ead.broker.exchange.userEventExchange}",
                            type = ExchangeTypes.FANOUT,
                            ignoreDeclarationExceptions = "true"
                    )
            )
    )
    public void listenUserEvent(@Payload final UserEventDTO userEventDTO) {
        final UserModel userModel = userEventDTO.convertToUserModel();

        switch (ActionType.valueOf(userEventDTO.getActionType())) {
            case CREATE:
                userModel.setPaymentStatus(PaymentStatus.NOTSTARTED);
                this.userService.save(userModel);
                break;

            case UPDATE:
                this.userService.save(userModel);
                break;

            case DELETE:
                this.userService.delete(userModel.getUserId());
                break;
        }
    }

}
