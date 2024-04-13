package com.mordent.ua.mediaservice.model.exception;

import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import lombok.Getter;

@Getter
public class MediaException extends RuntimeException {

    private final ErrorCode errorCode;

    public MediaException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
