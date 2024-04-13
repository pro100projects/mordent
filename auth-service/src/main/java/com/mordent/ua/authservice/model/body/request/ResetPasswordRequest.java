package com.mordent.ua.authservice.model.body.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record ResetPasswordRequest(
        @Email(message = "Email is not valid")
        @NotBlank(message = "Email cannot be blank")
        String email,
        @NotNull(message = "UUID cannot be null")
        UUID uuid,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 64, message = "Password must be of 8 - 64 characters")
        @Pattern(message = "Password must be have uppercase letter, lowercase letter, number and special character", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$")
        String password
) {
}
