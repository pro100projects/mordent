package com.mordent.ua.authservice.model.body.response;

public record AuthorizationResponse(
        String accessToken,
        String refreshToken
) {
}
