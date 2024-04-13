package com.mordent.ua.mediaservice.model.body.response;

import java.time.Instant;

public record UserAvatarResponse(
        Long id,
        String avatar,
        Instant updatedAt
) {
}
