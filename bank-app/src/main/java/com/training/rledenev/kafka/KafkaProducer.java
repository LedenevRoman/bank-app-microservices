package com.training.rledenev.kafka;

import com.training.rledenev.dto.AgreementDto;
import com.training.rledenev.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private static final String AGREEMENT_TOPIC = "agreement-topic";
    private static final String TRANSACTION_TOPIC = "transaction-topic";
    private final KafkaTemplate<String, AgreementDto> agreementKafkaTemplate;
    private final KafkaTemplate<String, TransactionDto> transactionKafkaTemplate;


    public void sendAgreement(AgreementDto agreementDto) {
        String messageKey = UUID.randomUUID().toString();
        ProducerRecord<String, AgreementDto> producerRecord =
                new ProducerRecord<>(AGREEMENT_TOPIC, messageKey, agreementDto);
        log.info("Sending message: message key - {}, agreement id - {}",
                messageKey, agreementDto.getId());
        log.info("to kafka topic - {}", AGREEMENT_TOPIC);
        agreementKafkaTemplate.send(producerRecord);
    }

    public void sendTransaction(TransactionDto transactionDto) {
        String messageKey = UUID.randomUUID().toString();
        ProducerRecord<String, TransactionDto> producerRecord =
                new ProducerRecord<>(TRANSACTION_TOPIC, messageKey, transactionDto);
        log.info("Sending message: message key - {}, transaction id - {}",
                messageKey, transactionDto.getId());
        log.info("to kafka topic - {}", AGREEMENT_TOPIC);
        transactionKafkaTemplate.send(producerRecord);
    }
}
