package com.mordent.ua.mediaservice.model.data;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

import static com.mordent.ua.mediaservice.utils.Nulls.getOrDefault;

@Table(name = "users")
@Builder(toBuilder = true)
public record User(
        @Id Long id,
        String name,
        String surname,
        String username,
        String email,
        String avatar,
        String password,
        Instant createdAt,
        Instant updatedAt,
        boolean enabled
) {

    public User overwritingVariables(User user) {
        return this.toBuilder()
                .name(getOrDefault(user.name, this.name))
                .surname(getOrDefault(user.surname, this.surname))
                .username(getOrDefault(user.username, this.username))
                .avatar(getOrDefault(user.avatar, this.avatar))
                .build();
    }
}
