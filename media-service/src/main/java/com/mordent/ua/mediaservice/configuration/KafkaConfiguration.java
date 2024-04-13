package com.mordent.ua.mediaservice.configuration;

import com.mordent.ua.mediaservice.kafka.event.SongEvent;
import com.mordent.ua.mediaservice.kafka.producer.SongKafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    private final String bootstrapServers;
    private final String songSaveTopic;
    private final String songListenTopic;
    private final String songDeleteTopic;

    public KafkaConfiguration(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.producer.topics.save-song}") String songSaveTopic,
            @Value("${spring.kafka.producer.topics.listen-song}") String songListenTopic,
            @Value("${spring.kafka.producer.topics.delete-song}") String songDeleteTopic
    ) {
        this.bootstrapServers = bootstrapServers;
        this.songSaveTopic = songSaveTopic;
        this.songListenTopic = songListenTopic;
        this.songDeleteTopic = songDeleteTopic;
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }

    @Bean
    public ProducerFactory<String, SongEvent> producerSongEventFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, SongEvent> kafkaSongEventTemplate(final ProducerFactory<String, SongEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Long> producerLongFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, Long> kafkaLongTemplate(final ProducerFactory<String, Long> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public SongKafkaProducer songKafkaProducer(final KafkaTemplate<String, SongEvent> kafkaSongEventTemplate, final KafkaTemplate<String, Long> kafkaLongTemplate) {
        return new SongKafkaProducer(songSaveTopic, songListenTopic, songDeleteTopic, kafkaSongEventTemplate, kafkaLongTemplate);
    }
}
