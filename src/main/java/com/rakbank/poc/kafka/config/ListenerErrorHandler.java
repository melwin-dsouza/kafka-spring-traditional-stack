package com.rakbank.poc.kafka.config;

import com.rakbank.poc.kafka.data.User;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class ListenerErrorHandler {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, User> kafkaTemplate) {
//        DeadLetterPublishingRecoverer recoverer =
//                new DeadLetterPublishingRecoverer(kafkaTemplate);

        return new DefaultErrorHandler(new FixedBackOff(5000L, 3));
//        return new DefaultErrorHandler(recoverer, new ExponentialBackOff(2000L, 5));

    }

//    @Bean
//    public KafkaListenerErrorHandler eh(DeadLetterPublishingRecoverer recoverer) {
//        return (msg, ex) -> {
//            if (msg.getHeaders().get(KafkaHeaders.DELIVERY_ATTEMPT, Integer.class) > 3) {
//                recoverer.accept(msg.getHeaders().get(KafkaHeaders.RAW_DATA, ConsumerRecord.class), ex);
//                return "FAILED";
//            }
//            throw ex;
//        };
//    }

}
