package com.mordent.ua.authservice.service;

import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    AuthorizationResponse login(AuthorizationRequest request, User user);

    User save(User user);

    User saveOauth2User(User user);

    User forgotPassword(String login);

    User resetPassword(String email, UUID uuid, String password);

    AuthorizationResponse generateTokens(User user);

    AuthorizationResponse refresh(String token);

    boolean validate(String token);

    User activate(UUID uuid);

    User findByEmailAndToken(String email, String token);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameIsOrEmailIs(String username, String email);
}
