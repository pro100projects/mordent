package com.mordent.ua.mediaservice.model.data;

import com.mordent.ua.mediaservice.model.domain.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder(toBuilder = true)
public record UserSecurity(
        Long id,
        String name,
        String surname,
        String username,
        String email,
        String password,
        Instant createdAt,
        Instant updatedAt,
        boolean enabled,
        //OneToMany
        Set<Role> roles
) {
}
