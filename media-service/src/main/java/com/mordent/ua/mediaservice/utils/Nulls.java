package com.mordent.ua.mediaservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Nulls {

    public static <T> T getOrThrow(final T value) {
        return getOrThrow(value, "Value is null");
    }

    public static <T> T getOrThrow(final T value, final String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
        return value;
    }

    public static <T> T getOrDefault(final T value, final T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static <T> T getOrNull(final T value) {
        return value;
    }
}
