package com.mordent.ua.authservice.model.body.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotBlank(message = "Surname cannot be blank")
        String surname,
        @NotBlank(message = "Username cannot be blank")
        String username,
        @Email(message = "Email is not valid")
        @NotBlank(message = "Email cannot be blank")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 64, message = "Password must be of 8 - 64 characters")
        @Pattern(message = "Password must be have uppercase letter, lowercase letter, number and special character", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$")
        String password,
        boolean artist,
        String token
) {
}
