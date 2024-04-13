package com.mordent.ua.mediaservice.model.data;

import java.time.Instant;

public record Like(
        Long id,
        boolean liked,
        Instant timestamp
) {
}
