package com.mordent.ua.mediaservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.domain.ErrorHttpResponse;
import com.mordent.ua.mediaservice.model.domain.ErrorResponse;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@Order(-1)
@Component
@RequiredArgsConstructor
public class WebFluxExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        if (ex instanceof MediaException mediaException) {
            log.info("Error intercepted", keyValue("errorCode", (mediaException.getErrorCode())), mediaException);
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            if (responseStatusException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.info("Intercepted client error", keyValue("statusCode", responseStatusException.getStatusCode()));
            }
        } else {
            log.error("Error intercepted", keyValue("errorCode", ErrorCode.UNEXPECTED), ex);
        }
        final ErrorHttpResponse errorHttpResponse = resolveException(ex);
        final ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errorHttpResponse.status());
        try {
            final String body = objectMapper.writeValueAsString(errorHttpResponse.body());
            return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
        } catch (JsonProcessingException exception) {
            log.error("Failed creating error response body");
        }
        return response.setComplete();
    }

    private ErrorHttpResponse resolveException(final Throwable ex) {
        if (ex instanceof MediaException mediaException) {
            return handleAuthException(mediaException);
        } else {
            return handleException(ex);
        }
    }

    private ErrorHttpResponse handleAuthException(final MediaException ex) {
        return new ErrorHttpResponse(
                HttpStatus.BAD_REQUEST,
                createErrorResponse(ex.getErrorCode(), ex.getMessage())
        );
    }

    private ErrorHttpResponse handleException(final Throwable ex) {
        return new ErrorHttpResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                createErrorResponse(ErrorCode.UNEXPECTED, ex.getMessage())
        );
    }

    private ErrorResponse createErrorResponse(final ErrorCode errorCode, final String description) {
        final Date timestamp = new Date();
        return new ErrorResponse(
                new ErrorResponse.Error(errorCode.getFullErrorCode(), description),
                timestamp
        );
    }
}
