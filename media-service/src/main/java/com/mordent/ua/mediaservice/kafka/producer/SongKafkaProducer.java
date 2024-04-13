package com.mordent.ua.mediaservice.kafka.producer;

import com.mordent.ua.mediaservice.kafka.event.SongEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
public class SongKafkaProducer {

    private final String songSaveTopic;
    private final String songListenTopic;
    private final String songDeleteTopic;
    private final KafkaTemplate<String, SongEvent> kafkaSongEventTemplate;
    private final KafkaTemplate<String, Long> kafkaLongTemplate;

    public Mono<Void> sendSaveEvent(final SongEvent event) {
        try {
            var result = kafkaSongEventTemplate.send(songSaveTopic, event).get(5, TimeUnit.SECONDS);
            log.info(String.format(
                    "Produced topic=%s, partition=%d, offset=%d, message: %s",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    event
            ));
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
        return Mono.empty();
    }

    public Mono<Void> sendListenEvent(final SongEvent event) {
        try {
            var result = kafkaSongEventTemplate.send(songListenTopic, event).get(5, TimeUnit.SECONDS);
            log.info(String.format(
                    "Produced topic=%s, partition=%d, offset=%d, message: %s",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    event
            ));
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
        return Mono.empty();
    }

    public Mono<Void> sendDeleteEvent(final Long id) {
        try {
            var result = kafkaLongTemplate.send(songDeleteTopic, id).get(5, TimeUnit.SECONDS);
            log.info(String.format(
                    "Produced topic=%s, partition=%d, offset=%d, message: %s",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    id
            ));
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new RuntimeException(ex);
        }
        return Mono.empty();
    }
}
