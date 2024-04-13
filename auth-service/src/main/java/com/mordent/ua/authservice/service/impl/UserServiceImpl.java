package com.mordent.ua.authservice.service.impl;

import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.model.exception.ErrorCode;
import com.mordent.ua.authservice.repository.UserRepository;
import com.mordent.ua.authservice.security.JwtProvider;
import com.mordent.ua.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthorizationResponse login(final AuthorizationRequest request, final User user) {
        if (!user.isEnabled()) {
            throw new AuthException(ErrorCode.USER_NOT_ENABLED, "User is not enabled");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException(ErrorCode.PASSWORD_INCORRECT, "Incorrect password");
        }
        return jwtProvider.generateTokens(user);
    }

    @Override
    public User save(final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider("MORDENT");
        user.setUuid(UUID.randomUUID());
        return userRepository.save(user);
    }

    @Override
    public User saveOauth2User(final User user) {
        if (user.getProvider().equals("GOOGLE")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public User forgotPassword(final String login) {
        User user = userRepository.findByUsernameIsOrEmailIs(login, login)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        user.setUuid(UUID.randomUUID());
        return userRepository.save(user);
    }

    @Override
    public User resetPassword(final String email, final UUID uuid, final String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        if (!user.isEnabled()) {
            throw new AuthException(ErrorCode.USER_NOT_ENABLED, "User is not enabled");
        } else if (user.getUuid() == null) {
            throw new AuthException(ErrorCode.INCORRECT_SECURITY_DATA, "User did not request a password change request");
        } else if (!user.getUuid().equals(uuid)) {
            throw new AuthException(ErrorCode.INCORRECT_SECURITY_DATA, "Uuid is incorrect");
        }
        user.setUuid(null);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public AuthorizationResponse generateTokens(final User user) {
        return jwtProvider.generateTokens(user);
    }

    @Override
    public AuthorizationResponse refresh(final String token) {
        Long userId = jwtProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        return jwtProvider.generateTokens(user);
    }

    @Override
    public boolean validate(final String token) {
        try {
            return jwtProvider.validateToken(token);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public User activate(final UUID uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        if (user.isEnabled()) {
            throw new AuthException(ErrorCode.USER_IS_ALREADY_ENABLED, "User is already enabled");
        }
        user.setEnabled(true);
        user.setUuid(null);
        return userRepository.save(user);
    }

    @Override
    public User findByEmailAndToken(final String email, final String token) {
        return userRepository.findByEmailAndToken(email, token)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
    }

    @Override
    public Optional<User> findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsernameIsOrEmailIs(final String username, final String email) {
        return userRepository.findByUsernameIsOrEmailIs(username, email);
    }
}
