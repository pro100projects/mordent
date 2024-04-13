package com.mordent.ua.mediaservice.model.body.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserPasswordUpdateRequest(
        Long id,
        @NotBlank(message = "Old password cannot be blank")
        @Size(min = 8, max = 64, message = "Old password must be of 8 - 64 characters")
        @Pattern(message = "Old password must be have uppercase letter, lowercase letter, number and special character", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$")
        String oldPassword,
        @NotBlank(message = "New password cannot be blank")
        @Size(min = 8, max = 64, message = "New password must be of 8 - 64 characters")
        @Pattern(message = "New password must be have uppercase letter, lowercase letter, number and special character", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,64}$")
        String newPassword
) {
}
