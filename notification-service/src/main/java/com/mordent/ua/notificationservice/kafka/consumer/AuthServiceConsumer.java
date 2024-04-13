package com.mordent.ua.notificationservice.kafka.consumer;

import com.mordent.ua.notificationservice.configuration.property.ApplicationProperties;
import com.mordent.ua.notificationservice.kafka.event.UserEvent;
import com.mordent.ua.notificationservice.service.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import static com.mordent.ua.notificationservice.utils.Logs.event;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceConsumer {

    private final EmailSender emailSender;
    private final ApplicationProperties applicationProperties;

    @KafkaListener(topics = "${spring.kafka.consumer.topics.registration}", containerFactory = "authServiceFactory")
    void consumeRegistrationEvent(final UserEvent event) {
        log.info("Consumed registration-event in AuthServiceConsumer -> {}", event, event(event));
        Context context = new Context();
        context.setVariable("user", event);
        context.setVariable("link", applicationProperties.host());
        emailSender.sendEmail(event.email(), "registration", context)
                .doOnSuccess(x -> log.info("Email about registration sent successfully"))
                .subscribe();
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.activate}", containerFactory = "authServiceFactory")
    void consumeActivateEvent(final UserEvent event) {
        log.info("Consumed activate-event in AuthServiceConsumer -> {}", event, event(event));
        Context context = new Context();
        context.setVariable("user", event);
        emailSender.sendEmail(event.email(), "activate", context)
                .doOnSuccess(x -> log.info("Email about successful activate user account sent successfully"))
                .subscribe();
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.forgot-password}", containerFactory = "authServiceFactory")
    void consumeResetPasswordEvent(final UserEvent event) {
        log.info("Consumed forgot-password-event in AuthServiceConsumer -> {}", event, event(event));
        Context context = new Context();
        context.setVariable("user", event);
        context.setVariable("link", applicationProperties.host());
        emailSender.sendEmail(event.email(), "forgot-password", context)
                .doOnSuccess(x -> log.info("Email about forgot password sent successfully"))
                .subscribe();
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topics.reset-password}", containerFactory = "authServiceFactory")
    void consumeResetPasswordSuccessfulEvent(final UserEvent event) {
        log.info("Consumed reset-password-event in AuthServiceConsumer -> {}", event, event(event));
        Context context = new Context();
        context.setVariable("user", event);
        emailSender.sendEmail(event.email(), "reset-password", context)
                .doOnSuccess(x -> log.info("Email about reset password sent successfully"))
                .subscribe();
    }
}
