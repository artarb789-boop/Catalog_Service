package com.kazakov.catalog.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topicName, String key, Object value) throws ExecutionException, InterruptedException {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topicName, key, value);
    }
}
