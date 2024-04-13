package com.mordent.ua.mediaservice.model.domain;

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
