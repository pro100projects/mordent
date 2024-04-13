package com.mordent.ua.mediaservice.model.domain;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNEXPECTED(ErrorCategory.MEDIA, "001"),
    VALIDATION_ERROR(ErrorCategory.MEDIA, "002"),
    NOT_ALLOWED(ErrorCategory.MEDIA, "003"),

    USER_NOT_FOUND(ErrorCategory.MEDIA, "004"),
    SONG_NOT_FOUND(ErrorCategory.MEDIA, "005"),
    USER_USERNAME_EXIST(ErrorCategory.MEDIA, "006"),
    USER_PASSWORD_INVALID(ErrorCategory.MEDIA, "007"),
    PLAYLIST_NOT_FOUND(ErrorCategory.MEDIA, "008"),
    ALBUM_NOT_FOUND(ErrorCategory.MEDIA, "008");

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
