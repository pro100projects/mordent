package com.mordent.ua.authservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder(toBuilder = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "password", nullable = false)
    private String password;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "provider", nullable = false)
    private String provider;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @Column(name = "uuid", unique = true)
    private UUID uuid;
    @Column(name = "token", unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Role.class)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Role> roles;

    public User overwritingVariables(User user) {
        return this.toBuilder()
                .name(user.getName())
                .surname(user.getSurname())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .provider("GOOGLE")
                .enabled(true)
                .uuid(null)
                .token(null)
                .roles(user.roles.isEmpty() ? Set.of(Role.ROLE_USER) : user.roles)
                .build();
    }

    public static User getGoogleUser(final DefaultOidcUser user) {
        return User.builder()
                .name(user.getGivenName())
                .surname(user.getFamilyName())
                .username(user.getEmail())
                .email(user.getEmail())
                .avatar(user.getPicture())
                .password("password")
                .provider("GOOGLE_NOT_ACTIVATE")
                .build();
    }
}
