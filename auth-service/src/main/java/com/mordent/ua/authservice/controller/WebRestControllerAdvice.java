package com.mordent.ua.authservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.model.exception.ErrorCode;
import com.mordent.ua.authservice.model.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class WebRestControllerAdvice {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(final RuntimeException ex) {
        if (ex instanceof AuthException authException) {
            log.info("Error intercepted", keyValue("error_code", (authException.getErrorCode())), authException);
        } else {
            log.error("Error intercepted", keyValue("error_code", ErrorCode.UNEXPECTED), ex);
        }
        final ErrorResponse errorResponse = resolveException(ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(final MethodArgumentNotValidException ex) {
        final Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, field -> Objects.requireNonNull(field.getDefaultMessage())));
        final String description;
        try {
            description = objectMapper.writeValueAsString(fieldErrors);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        final ErrorResponse errorResponse = createErrorResponse(ErrorCode.VALIDATION_ERROR, description);
        return ResponseEntity.badRequest().body(errorResponse);
    }


    private ErrorResponse resolveException(final RuntimeException ex) {
        if (ex instanceof AuthException authException) {
            return handleAuthException(authException);
        } else {
            return handleException(ex);
        }
    }

    private ErrorResponse handleAuthException(final AuthException ex) {
        return createErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    private ErrorResponse handleException(final RuntimeException ex) {
        return createErrorResponse(ErrorCode.UNEXPECTED, ex.getMessage());
    }

    private ErrorResponse createErrorResponse(final ErrorCode errorCode, final String description) {
        final Date timestamp = new Date();
        return new ErrorResponse(
                new ErrorResponse.Error(errorCode.getFullErrorCode(), description),
                timestamp
        );
    }
}
