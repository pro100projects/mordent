package com.mordent.ua.notificationservice.service.impl;

import com.mordent.ua.notificationservice.service.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public Mono<Void> sendEmail(final String email, final String eventType, final Context context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String subject = getSubject(eventType);
            String template = getTemplate(eventType);

            helper.setFrom("mordent@gmail.com");
            helper.setTo(email);
            helper.setSubject(subject);
            String html = templateEngine.process(template, context);
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return Mono.empty();
    }

    private static String getSubject(final String eventType) {
        return switch (eventType) {
            case "registration" -> "Confirm Email Address";
            case "activate" -> "Successful registration";
            case "save-song" -> "Successful music saving";
            case "listen-song" -> "You have new auditions";
            case "forgot-password" -> "Forgot password";
            case "reset-password" -> "Reset password";
            default -> throw new UnsupportedOperationException(String.format("Event type '%s' is not is not supported", eventType));
        };
    }

    private static String getTemplate(final String eventType) {
        return switch (eventType) {
            case "registration" -> "registration.html";
            case "activate" -> "activate.html";
            case "save-song" -> "save-song.html";
            case "listen-song" -> "listen-song.html";
            case "forgot-password" -> "forgot-password.html";
            case "reset-password" -> "reset-password.html";
            default -> throw new UnsupportedOperationException(String.format("Event type '%s' is not is not supported", eventType));
        };
    }
}
