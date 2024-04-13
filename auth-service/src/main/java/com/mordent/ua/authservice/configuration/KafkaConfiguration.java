package com.mordent.ua.authservice.configuration;

import com.mordent.ua.authservice.kafka.event.UserEvent;
import com.mordent.ua.authservice.kafka.producer.AuthServiceProducer;
import com.mordent.ua.authservice.model.Qualifiers;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final String registrationTopic;
    private final String activateTopic;
    private final String forgotPassword;
    private final String resetPassword;

    public KafkaConfiguration(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${spring.kafka.producer.topics.registration}") String registrationTopic,
            @Value("${spring.kafka.producer.topics.activate}") String activateTopic,
            @Value("${spring.kafka.producer.topics.forgot-password}") String forgotPassword,
            @Value("${spring.kafka.producer.topics.reset-password}") String resetPassword
    ) {
        this.bootstrapServers = bootstrapServers;
        this.registrationTopic = registrationTopic;
        this.activateTopic = activateTopic;
        this.forgotPassword = forgotPassword;
        this.resetPassword = resetPassword;
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
    public ProducerFactory<String, UserEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, UserEvent> kafkaTemplate(ProducerFactory<String, UserEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier(Qualifiers.REGISTRATION_PRODUCER)
    public AuthServiceProducer authServiceRegistrationProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        return new AuthServiceProducer(registrationTopic, kafkaTemplate);
    }

    @Bean
    @Qualifier(Qualifiers.ACTIVATE_PRODUCER)
    public AuthServiceProducer authServiceActivateProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        return new AuthServiceProducer(activateTopic, kafkaTemplate);
    }

    @Bean
    @Qualifier(Qualifiers.FORGOT_PASSWORD_PRODUCER)
    public AuthServiceProducer authServiceForgotPasswordProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        return new AuthServiceProducer(forgotPassword, kafkaTemplate);
    }

    @Bean
    @Qualifier(Qualifiers.RESET_PASSWORD_PRODUCER)
    public AuthServiceProducer authServiceResetPasswordProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        return new AuthServiceProducer(resetPassword, kafkaTemplate);
    }
}
