package com.mordent.ua.authservice.repository;

import com.mordent.ua.authservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameIsOrEmailIs(String username, String email);

    Optional<User> findByUuid(UUID uuid);

    Optional<User> findByEmailAndToken(String email, String token);
}
