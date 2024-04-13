package com.mordent.ua.authservice.model.exception;

import java.util.Date;

public record ErrorResponse(
        Error error,
        Date timestamp
) {

    public record Error(
            String code,
            String description
    ) {
    }
}
