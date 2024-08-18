package com.training.rledenev.kafka;


import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.service.AgreementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final AgreementService agreementService;

    @KafkaListener(topics = "agreement-topic", groupId = "consumer-1")
    public void listen(@Payload AgreementDto agreementDto,
                       @Header(KafkaHeaders.RECEIVED_KEY) String receivedKey) {
        log.info("Received message: message key - {}, agreement id - {}",
                receivedKey, agreementDto.getId());
        log.info("from kafka topic - {}", "agreement-topic");
        agreementService.handleAgreement(agreementDto);

    }
}
