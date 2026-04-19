package com.illusion.ticketblitz.catalogservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    /**
     * Configures the Global Error Handler for Kafka Consumers.
     * 1. Retries processing 3 times, with a 2-second delay between attempts.
     * 2. If all retries fail, pushes the message to <original-topic>.DLT
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {

        // 1. The Recoverer: Tells Kafka where to send the message when retries are exhausted.
        // By default, DeadLetterPublishingRecoverer appends ".DLT" to the original topic name.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        // 2. The BackOff: 2000ms (2 seconds) interval, 3 maximum retries.
        // Note: This means 1 initial attempt + 3 retries = 4 total attempts.
        FixedBackOff backOff = new FixedBackOff(2000L, 3L);

        // 3. Create the Error Handler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // OPTIONAL BUT RECOMMENDED: Don't retry on fatal errors (like bad JSON formatting).
        // Go straight to the DLQ to save resources.
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                com.fasterxml.jackson.core.JsonProcessingException.class
        );

        // 4. Add a logger so you know when a retry is happening
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Retry failed. Attempt {}/4 for topic: {}, partition: {}, offset: {}. Error: {}",
                        deliveryAttempt, record.topic(), record.partition(), record.offset(), ex.getMessage())
        );

        return errorHandler;
    }
}