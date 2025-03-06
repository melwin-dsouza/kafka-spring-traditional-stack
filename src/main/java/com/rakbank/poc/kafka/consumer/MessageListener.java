package com.rakbank.poc.kafka.consumer;

import com.rakbank.poc.kafka.config.CcmClient;
import com.rakbank.poc.kafka.config.RestClient;
import com.rakbank.poc.kafka.config.RestTemplateConfig;
import com.rakbank.poc.kafka.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageListener {

    Logger log = LoggerFactory.getLogger(MessageListener.class);

    private final RestClient restClient;

    @Autowired
    public MessageListener(RestClient restClient) {
        this.restClient = restClient;
    }


    @KafkaListener(topics = "kafka-mvd",groupId = "test-group",concurrency = "4")
    public void consumeEvents(User user) {
        try {
            log.info("consumer consume the events {} ", user.toString());
            restClient.getUser(user);
        } catch (Exception e) {
            System.err.println("⚠️ Error processing Kafka message: " + e.getMessage());
        }

    }


}
