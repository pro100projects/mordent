package com.mordent.ua.authservice.interactor.impl;

import com.mordent.ua.authservice.interactor.AuthInteractor;
import com.mordent.ua.authservice.kafka.producer.AuthServiceProducer;
import com.mordent.ua.authservice.mapper.UserMapper;
import com.mordent.ua.authservice.model.Qualifiers;
import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.request.RegistrationRequest;
import com.mordent.ua.authservice.model.body.request.ResetPasswordRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.body.response.TokenValidateResponse;
import com.mordent.ua.authservice.model.body.response.UserResponce;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.model.exception.ErrorCode;
import com.mordent.ua.authservice.security.SecurityAppConfig;
import com.mordent.ua.authservice.service.RedirectService;
import com.mordent.ua.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthInteractorImpl implements AuthInteractor {

    private final UserMapper userMapper;
    private final UserService userService;
    private final RedirectService redirectService;
    @Qualifier(Qualifiers.REGISTRATION_PRODUCER)
    private final AuthServiceProducer authServiceRegistrationProducer;
    @Qualifier(Qualifiers.ACTIVATE_PRODUCER)
    private final AuthServiceProducer authServiceActivateProducer;
    @Qualifier(Qualifiers.FORGOT_PASSWORD_PRODUCER)
    private final AuthServiceProducer authServiceForgotPasswordProducer;
    @Qualifier(Qualifiers.RESET_PASSWORD_PRODUCER)
    private final AuthServiceProducer authServiceResetPasswordProducer;
    private final SecurityAppConfig securityAppConfig;

    @Override
    public AuthorizationResponse login(final AuthorizationRequest request) {
        User user = userService.findByUsernameIsOrEmailIs(request.login(), request.login())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        return userService.login(request, user);
    }

    @Override
    public AuthorizationResponse registration(final RegistrationRequest request) {
        if (userService.findByUsername(request.username()).isPresent()) {
            throw new AuthException(ErrorCode.USER_USERNAME_EXIST, "User with this username was already exist");
        }
        Optional<User> optionalUser = userService.findByEmail(request.email());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getProvider().equals("GOOGLE_NOT_ACTIVATE")) {
                if (user.getToken() == null || !user.getToken().equals(request.token())) {
                    throw new AuthException(ErrorCode.TOKEN_NOT_VALID, "Token is not valid");
                } else if (user.getUpdatedAt().isBefore(Instant.now().minus(1, ChronoUnit.HOURS))) {
                    throw new AuthException(ErrorCode.TOKEN_NOT_VALID, "Token is expire");
                }
                user = userService.saveOauth2User(user.overwritingVariables(userMapper.toEntity(request)));
                return userService.generateTokens(user);
            } else {
                throw new AuthException(ErrorCode.USER_EMAIL_EXIST, "User with this email was already exist");
            }
        }
        User user = userService.save(userMapper.toEntity(request));
        authServiceRegistrationProducer.send(userMapper.toEvent(user));
        return new AuthorizationResponse(null, null);
    }

    @Override
    public void forgotPassword(final String login) {
        User user = userService.forgotPassword(login);
        authServiceForgotPasswordProducer.send(userMapper.toEvent(user));
    }

    @Override
    public AuthorizationResponse resetPassword(final ResetPasswordRequest request) {
        User user = userService.resetPassword(request.email(), request.uuid(), request.password());
        authServiceResetPasswordProducer.send(userMapper.toEvent(user));
        return userService.generateTokens(user);
    }

    @Override
    public AuthorizationResponse refresh(final String token) {
        return userService.refresh(token);
    }

    @Override
    public TokenValidateResponse validate(final String token) {
        return new TokenValidateResponse(userService.validate(token));
    }

    @Override
    public RedirectView activate(final UUID uuid) {
        User user = userService.activate(uuid);
        authServiceActivateProducer.send(userMapper.toEvent(user));
        return redirectService.redirect(userService.generateTokens(user));
    }

    @Override
    public UserResponce getUserViaEmailAndGoogleToken(final String email, final String token) {
        User user = userService.findByEmailAndToken(email, token);
        if (user.getUpdatedAt().isBefore(Instant.now().minus(1, ChronoUnit.MINUTES))) {
            throw new AuthException(ErrorCode.TOKEN_NOT_VALID, "Token is expire");
        }
        return new UserResponce(user.getName(), user.getSurname(), user.getEmail());
    }

    @Override
    public RedirectView redirectToOauth2(final String requestUri, final String redirectUri) {
        final URI clientRequestUri = URI.create(requestUri);
        final String authorizedRequestHost = securityAppConfig.oauth2().authorizedRequestHosts().stream()
                .filter(uri -> {
                    URI authorizedURI = URI.create(uri);
                    return authorizedURI.getHost().equals(clientRequestUri.getHost()) && authorizedURI.getPort() == clientRequestUri.getPort();
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sorry! We've got an Unauthorized Request URI and can't proceed with the authentication"));
        final String uri = UriComponentsBuilder
                .fromUriString(securityAppConfig.oauth2().authorizedRequestHost().orElse(authorizedRequestHost))
                .path("/oauth2/authorize/google")
                .queryParam("redirect_uri", redirectUri)
                .toUriString();
        return new RedirectView(uri);
    }
}
