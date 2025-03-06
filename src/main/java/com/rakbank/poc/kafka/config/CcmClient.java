package com.rakbank.poc.kafka.config;

import com.rakbank.poc.kafka.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class CcmClient {

    WebClient webClient= WebClient.builder().build();
    Logger log = LoggerFactory.getLogger(CcmClient.class);


    public void callCCM(User user){
        user.setName("block-"+user.getName());
        log.info("Starting NON-BLOCKING Controller!");
        Mono<User> userMono = webClient.post()
                .uri("http://localhost:8088/sendEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class);
        log.info("Inside NON-BLOCKING Controller!");
        userMono.doOnNext(message -> log.info("Received message: {}" , message))
                .doOnError(error -> System.err.println("Error: " + error.getMessage())).
                subscribe();


    }
}
