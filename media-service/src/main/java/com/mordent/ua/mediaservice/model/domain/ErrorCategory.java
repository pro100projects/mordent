package com.mordent.ua.mediaservice.model.domain;

import lombok.Getter;

@Getter
public enum ErrorCategory {
    MEDIA("MEDIA100");

    private final String code;

    ErrorCategory(String code) {
        this.code = code;
    }
}
