package com.demo.services.messaging;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Component
public class SampleKafkaConsumerService {
    @KafkaListener(id = "javaclient", topics = "mytopic")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}

