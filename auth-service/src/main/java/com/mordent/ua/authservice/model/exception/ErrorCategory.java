package com.mordent.ua.authservice.model.exception;

import lombok.Getter;

@Getter
public enum ErrorCategory {
    AUTH("AUTH100");

    private final String code;

    ErrorCategory(String code) {
        this.code = code;
    }
}
