package com.training.kafka.spring.app.trading.config;


import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.UUID;

@Configuration
public class TransactionConfig {

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager(
            KafkaTransactionManager kafkaTransactionManager,
            JpaTransactionManager transactionManager) {
        return new ChainedKafkaTransactionManager<>(kafkaTransactionManager, transactionManager);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory consumerFactory,
            ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);
        return factory;
    }

    @Bean
    @Primary
    KafkaTemplate<String, String> kafkaTemplate(ProducerFactory producerFactory) {
        return new KafkaTemplate(producerFactory);
    }

    @Bean
    @Primary
    KafkaTransactionManager<String, String> kafkaTransactionManager(ProducerFactory producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean("standaloneKafkaTemplate")
    KafkaTemplate<String, String> standaloneKafkaTemplate(ProducerFactory producerFactory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate(producerFactory);
        kafkaTemplate.setTransactionIdPrefix(String.format("tx-%s-", UUID.randomUUID().toString()));
        return kafkaTemplate;
    }

    @Bean("standaloneKafkaTransactionManager")
    KafkaTransactionManager<String, String> standaloneKafkaTransactionManager(ProducerFactory producerFactory) {
        KafkaTransactionManager<String, String> kafkaTransactionManager = new KafkaTransactionManager<>(producerFactory);
        kafkaTransactionManager.setTransactionIdPrefix(String.format("tx-%s-", UUID.randomUUID().toString()));
        return kafkaTransactionManager;
    }
}