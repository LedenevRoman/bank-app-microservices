package com.training.rledenev.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newAgreementTopic() {
        return new NewTopic("agreement-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic newAccountTopic() {
        return new NewTopic("account-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic newTransactionTopic() {
        return new NewTopic("transaction-topic", 1, (short) 1);
    }
}
