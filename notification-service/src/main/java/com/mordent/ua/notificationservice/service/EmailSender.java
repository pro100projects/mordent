package com.mordent.ua.notificationservice.service;

import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

public interface EmailSender {

    Mono<Void> sendEmail(String email, String eventType, Context context);
}
