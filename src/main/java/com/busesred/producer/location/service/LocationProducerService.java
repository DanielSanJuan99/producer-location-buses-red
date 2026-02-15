package com.busesred.producer.location.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.busesred.producer.location.model.LocationMessage;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LocationProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.location}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key.location}")
    private String routingKey;

    public LocationProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendLocation(LocationMessage locationMessage) {
        log.info("Enviando ubicación a RabbitMQ: {}", locationMessage);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, locationMessage);
        log.info("Ubicación enviada exitosamente");
    }
}
