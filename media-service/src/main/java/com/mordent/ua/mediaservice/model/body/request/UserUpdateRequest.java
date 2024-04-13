package com.mordent.ua.mediaservice.model.body.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserUpdateRequest(
        Long id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotBlank(message = "Surname cannot be blank")
        String surname,
        @NotBlank(message = "Username cannot be blank")
        String username
) {
}
