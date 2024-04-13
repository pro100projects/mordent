package com.mordent.ua.mediaservice.model.domain;

import org.springframework.http.HttpStatus;

public record ErrorHttpResponse(
        HttpStatus status,
        ErrorResponse body
) {
}
