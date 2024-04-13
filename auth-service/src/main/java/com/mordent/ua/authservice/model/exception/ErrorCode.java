package com.mordent.ua.authservice.model.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNEXPECTED(ErrorCategory.AUTH, "001"),
    VALIDATION_ERROR(ErrorCategory.AUTH, "002"),

    USER_NOT_FOUND(ErrorCategory.AUTH, "003"),
    USER_USERNAME_EXIST(ErrorCategory.AUTH, "004"),
    USER_EMAIL_EXIST(ErrorCategory.AUTH, "005"),
    PASSWORD_INCORRECT(ErrorCategory.AUTH, "006"),
    TOKEN_NOT_VALID(ErrorCategory.AUTH, "007"),
    USER_NOT_ENABLED(ErrorCategory.AUTH, "008"),
    USER_IS_ALREADY_ENABLED(ErrorCategory.AUTH, "009"),
    INCORRECT_SECURITY_DATA(ErrorCategory.AUTH, "010");

    private final ErrorCategory errorCategory;
    private final String code;

    ErrorCode(ErrorCategory errorCategory, String code) {
        this.errorCategory = errorCategory;
        this.code = code;
    }

    public String getFullErrorCode() {
        return errorCategory.getCode() + code;
    }
}
