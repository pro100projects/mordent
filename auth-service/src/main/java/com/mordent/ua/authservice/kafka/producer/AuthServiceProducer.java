package com.mordent.ua.authservice.kafka.producer;

import com.mordent.ua.authservice.kafka.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
public class AuthServiceProducer {

    private final String topic;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void send(final UserEvent event) {
        try {
            var result = kafkaTemplate.send(topic, event).get(5, TimeUnit.SECONDS);
            log.info(String.format(
                    "Produced topic=%s, partition=%d, offset=%d, message: %s",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    event
            ));
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
