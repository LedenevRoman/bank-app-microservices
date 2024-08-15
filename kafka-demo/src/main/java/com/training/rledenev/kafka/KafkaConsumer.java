package com.training.rledenev.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "bank-app", groupId = "consumer-1")
    public void listen(String message) {
        log.info("message received");
        log.info(message);
    }
}
