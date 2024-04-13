package com.mordent.ua.authservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Nulls {

    public static <T> T getOrDefault(final T value, final T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static <T> T getOrNull(final T value) {
        return value;
    }
}
