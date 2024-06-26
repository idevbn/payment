package com.ead.payment.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    private final CachingConnectionFactory cachingConnectionFactory;

    @Value("${ead.broker.exchange.paymentEventExchange}")
    private String exchangePaymentEvent;

    @Autowired
    public RabbitmqConfig(final CachingConnectionFactory cachingConnectionFactory) {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        final RabbitTemplate template = new RabbitTemplate(this.cachingConnectionFactory);
        template.setMessageConverter(this.messageConverter());
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public FanoutExchange fanoutPaymentEvent() {
        return new FanoutExchange(this.exchangePaymentEvent);
    }

}
