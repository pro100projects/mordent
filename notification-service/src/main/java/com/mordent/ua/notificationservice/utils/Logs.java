package com.mordent.ua.notificationservice.utils;

import lombok.experimental.UtilityClass;
import net.logstash.logback.argument.StructuredArgument;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@UtilityClass
public class Logs {

    public final static String KEY_TOPIC = "topic";
    public final static String KEY_EVENT = "event";

    public static StructuredArgument topic(final String topic) {
        return keyValue(KEY_TOPIC, topic);
    }

    public static StructuredArgument event(final Object event) {
        return keyValue(KEY_EVENT, event);
    }
}
