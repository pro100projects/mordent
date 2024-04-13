package com.mordent.ua.metadataservice.kafka.consumer;

import com.mordent.ua.metadataservice.kafka.event.SongEvent;
import com.mordent.ua.metadataservice.service.FileService;
import com.mordent.ua.metadataservice.service.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongKafkaConsumer {

    private final SongService songService;
    private final FileService fileService;

    @KafkaListener(topics = "${spring.kafka.consumer.topics.save-song}", containerFactory = "songSaveFactory")
    void consume(final SongEvent event) {
        log.info("Consumed save-song-event in SongKafkaConsumer -> {}", event);
        fileService.getSongMetadata(event.song().id(), event.song().filepath())
                .flatMap(songService::save)
                .subscribe(song -> log.info("SongMetadata has been saved: {}", song));
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.delete-song}", containerFactory = "songDeleteFactory")
    void consume(final Long songId) {
        log.info("Consumed delete-song-event in SongKafkaConsumer -> {}", songId);
        songService.delete(songId)
                .doOnSuccess(song -> log.info("SongMetadata with song id: {} has been deleted", songId))
                .subscribe();
    }
}
