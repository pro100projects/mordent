package com.mordent.ua.notificationservice.kafka.consumer;

import com.mordent.ua.notificationservice.kafka.event.SongEvent;
import com.mordent.ua.notificationservice.service.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongKafkaConsumer {

    private final EmailSender emailSender;

    @KafkaListener(topics = "${spring.kafka.consumer.topics.save-song}", containerFactory = "songFactory")
    void consumeSaveEvent(final SongEvent event) {
        log.info("Consumed event in SongKafkaConsumer -> {}", event);
        Context context = new Context();
        context.setVariable("user", event.user());
        context.setVariable("song", event.song());
        emailSender.sendEmail(event.user().email(), "save-song", context)
                .doOnSuccess(x -> log.info("Email about saved music sent successfully"))
                .subscribe();
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.listen-song}", containerFactory = "songFactory")
    void consumeListenEvent(final SongEvent event) {
        log.info("Consumed event in SongKafkaConsumer -> {}", event);
        Context context = new Context();
        context.setVariable("user", event.user());
        context.setVariable("song", event.song());
        emailSender.sendEmail(event.user().email(), "listen-song", context)
                .doOnSuccess(x -> log.info("Email about listening to a music sent successfully"))
                .subscribe();
    }
}
