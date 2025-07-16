package com.local.core.user.service;

import static com.local.core.user.config.RequestLoggingInterceptor.LOG_REQUEST_ID_NAME;
import static com.local.core.user.config.UserLoggingInterceptor.LOG_USER_ID_NAME;
import static org.slf4j.MDC.get;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    final KafkaTemplate<String, String> kafkaTemplate;
    final ObjectMapper mapper;

    @Async
    public CompletableFuture<SendResult<String, String>> sendMessage(
            String topic, Object raw) {
        try {
            return sendMessage(topic, mapper.writeValueAsString(raw));
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<SendResult<String, String>> sendMessage(
            String topic, String message) {
        return kafkaTemplate.send(buildMessage(topic, message))
                .toCompletableFuture();
    }

    Message<String> buildMessage(String topic, String payload) {
        return MessageBuilder
                .withPayload(payload)
                .setHeader(TOPIC, topic)
                .setHeader(LOG_REQUEST_ID_NAME, get(LOG_REQUEST_ID_NAME))
                .setHeader(LOG_USER_ID_NAME, get(LOG_USER_ID_NAME))
                .build();
    }
}
