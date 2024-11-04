package com.training.rledenev.kafka;


import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.dto.TransactionDto;
import com.training.rledenev.service.AgreementService;
import com.training.rledenev.service.TransactionService;
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
    private final TransactionService transactionService;

    @KafkaListener(topics = "agreement-topic", groupId = "consumer-1")
    public void listenAgreement(@Payload AgreementDto agreementDto,
                       @Header(KafkaHeaders.RECEIVED_KEY) String receivedKey) {
        log.info("Received message: message key - {}, agreement id - {}",
                receivedKey, agreementDto.getId());
        log.info("from kafka topic - {}", "agreement-topic");
        agreementService.handleAgreement(agreementDto);

    }

    @KafkaListener(topics = "transaction-topic", groupId = "consumer-1")
    public void listenTransaction(@Payload TransactionDto transactionDto,
                       @Header(KafkaHeaders.RECEIVED_KEY) String receivedKey) {
        log.info("Received message: message key - {}, agreement id - {}",
                receivedKey, transactionDto.getId());
        log.info("from kafka topic - {}", "agreement-topic");
        transactionService.handleTransaction(transactionDto);

    }
}
