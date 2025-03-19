package com.rakbank.poc.kafka.config;

import com.rakbank.poc.kafka.data.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.converter.ConversionException;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;

@Configuration
@Slf4j
public class ListenerContainerErrorHandler {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, User> template) {
        // Exponential Backoff: initial delay 2s, multiplier 2x, max delay 30s, max retries 5
        ExponentialBackOff backOff = new ExponentialBackOff();
        backOff.setInitialInterval(2000L); // Start with 2 seconds delay
        backOff.setMultiplier(2.0); // Double the delay each time
        backOff.setMaxInterval(30000L); // Max retry delay: 30 seconds
        backOff.setMaxAttempts(3); // Retry 3 times before giving up

        // Logging only (No Dead Letter Queue)
//        ConsumerRecordRecoverer recoverer = (record, ex) -> {
//            log.error("🔥 Message permanently failed after retries: Key={}, Value={}, Partition={}, Offset={}",
//                    record.key(), record.value(), record.partition(), record.offset());
//        };

        //Dead letter Queue
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, exception) -> {
                    if (exception.getCause() instanceof ResourceAccessException) {
                        return new TopicPartition(record.topic() + "-Error", record.partition());
                    }
                    return null; // Other exceptions won't go to DLQ
                });

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer,backOff);

        // ✅ Add retriable exception types
        errorHandler.addRetryableExceptions(
                RuntimeException.class, // Your application errors
                ListenerExecutionFailedException.class // Kafka listener failures
        );

        // ❌ Do NOT retry for deserialization errors
        errorHandler.addNotRetryableExceptions(
                DeserializationException.class,
                MessageConversionException.class,
                ConversionException.class
        );

        return errorHandler;
    }


}
