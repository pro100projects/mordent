package com.mordent.ua.mediaservice.repository.extension.impl;

import com.mordent.ua.mediaservice.model.data.UserSecurity;
import com.mordent.ua.mediaservice.model.domain.Role;
import com.mordent.ua.mediaservice.repository.extension.UserRepositoryExtension;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mordent.ua.mediaservice.utils.Nulls.getOrThrow;

@RequiredArgsConstructor
public class UserRepositoryExtensionImpl implements UserRepositoryExtension {

    private static final String QUERY_FIND_BY_USER_ID = """
            SELECT u.id AS u_id, u.name AS u_name, u.surname AS u_surname, u.username AS u_username, u.email AS u_email, u.password AS u_password,
            u.created_at AS u_created_at, u.updated_at AS u_updated_at, u.enabled AS u_enabled, ur.role AS ur_role
            FROM users AS u
            INNER JOIN user_roles AS ur ON u.id = ur.user_id
            WHERE u.id=:userId
            """;

    private final DatabaseClient databaseClient;

    @Override
    public Mono<UserSecurity> findByUserId(final Long userId) {
        return databaseClient.sql(QUERY_FIND_BY_USER_ID)
                .bind("userId", userId)
                .fetch()
                .all()
                .reduce(new HashMap<>(), this::mapToUserSecurity)
                .flatMap(userSecurityMap -> userSecurityMap.isEmpty() ? Mono.empty() : Mono.just(mapUserSecurityMapToUserSecurity(userSecurityMap)));
    }

    private Map<Long, UserSecurity> mapToUserSecurity(final Map<Long, UserSecurity> accumulator, final Map<String, Object> row) {
        final var userSecurity = new UserSecurity(
                (Long) getOrThrow(row.get("u_id")),
                (String) getOrThrow(row.get("u_name")),
                (String) getOrThrow(row.get("u_surname")),
                (String) getOrThrow(row.get("u_username")),
                (String) getOrThrow(row.get("u_email")),
                (String) getOrThrow(row.get("u_password")),
                ((OffsetDateTime) getOrThrow(row.get("u_created_at"))).toInstant(),
                ((OffsetDateTime) getOrThrow(row.get("u_updated_at"))).toInstant(),
                (Boolean) getOrThrow(row.get("u_enabled")),
                Set.of(Role.valueOf((String) getOrThrow(row.get("ur_role"))))
        );
        accumulator.put((long) accumulator.size(), userSecurity);
        return accumulator;
    }

    private UserSecurity mapUserSecurityMapToUserSecurity(final Map<Long, UserSecurity> userSecurityMap) {
        UserSecurity userSecurity = userSecurityMap.values().stream().findFirst().orElseThrow();
        Set<Role> roles = userSecurityMap.values().stream()
                .map(UserSecurity::roles)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        return userSecurity.toBuilder().roles(roles).build();
    }
}
