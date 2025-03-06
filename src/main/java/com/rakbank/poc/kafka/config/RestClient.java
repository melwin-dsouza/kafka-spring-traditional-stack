package com.rakbank.poc.kafka.config;

import com.rakbank.poc.kafka.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestClient {

    @Autowired
    private RestTemplate restTemplate;

    Logger log = LoggerFactory.getLogger(RestClient.class);

    public User getUser(User user){
        log.info("Starting BLOCKING Controller!");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<User> response = restTemplate.exchange(
                "http://localhost:8088/sendEmail", HttpMethod.POST, requestEntity,
                User.class);
        log.info("Ending BLOCKING Controller!");
        return response.getBody();

    }
}
