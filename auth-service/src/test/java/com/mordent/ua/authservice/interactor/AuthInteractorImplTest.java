package com.mordent.ua.authservice.interactor;

import com.mordent.ua.authservice.interactor.impl.AuthInteractorImpl;
import com.mordent.ua.authservice.kafka.event.UserEvent;
import com.mordent.ua.authservice.kafka.producer.AuthServiceProducer;
import com.mordent.ua.authservice.mapper.UserMapper;
import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.request.RegistrationRequest;
import com.mordent.ua.authservice.model.body.request.ResetPasswordRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.body.response.TokenValidateResponse;
import com.mordent.ua.authservice.model.body.response.UserResponce;
import com.mordent.ua.authservice.model.entity.Role;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.model.exception.ErrorCode;
import com.mordent.ua.authservice.service.RedirectService;
import com.mordent.ua.authservice.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.mordent.ua.authservice.util.Nulls.getOrNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInteractorImplTest {

    @InjectMocks
    private AuthInteractorImpl authInteractor;

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @Mock
    private RedirectService redirectService;
    @Mock
    private AuthServiceProducer authServiceProducer;

    //region login

    @Test
    void whenLoginWithExistUserEmailExpectToSucceed() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user@gmail.com", "12345678");
        final User user = generateUser();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(userService.findByUsernameIsOrEmailIs(request.login(), request.login())).thenReturn(Optional.of(user));
        when(userService.login(request, user)).thenReturn(response);
        assertEquals(response, authInteractor.login(request));
        verify(userService, times(1)).findByUsernameIsOrEmailIs(request.login(), request.login());
        verify(userService, times(1)).login(request, user);
    }

    @Test
    void whenLoginWithExistUserUsernameExpectToSucceed() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user", "12345678");
        final User user = generateUser();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(userService.findByUsernameIsOrEmailIs(request.login(), request.login())).thenReturn(Optional.of(user));
        when(userService.login(request, user)).thenReturn(response);
        assertEquals(response, authInteractor.login(request));
        verify(userService, times(1)).findByUsernameIsOrEmailIs(request.login(), request.login());
        verify(userService, times(1)).login(request, user);
    }

    @Test
    void whenLoginWithNotExistUserLoginExpectToThrowError() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user1", "12345678");

        when(userService.findByUsernameIsOrEmailIs(request.login(), request.login())).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> authInteractor.login(request));
        verify(userService, times(1)).findByUsernameIsOrEmailIs(request.login(), request.login());
    }

    @Test
    void whenLoginWithNotEnabledUserExpectToThrowError() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user", "12345678");
        final User user = generateUser();

        when(userService.findByUsernameIsOrEmailIs(request.login(), request.login())).thenReturn(Optional.of(user));
        when(userService.login(request, user)).thenThrow(new AuthException(ErrorCode.USER_NOT_ENABLED, "User is not enabled"));
        assertThrows(AuthException.class, () -> authInteractor.login(request), "User is not enabled");
        verify(userService, times(1)).findByUsernameIsOrEmailIs(request.login(), request.login());
        verify(userService, times(1)).login(request, user);
    }

    @Test
    void whenLoginWithIncorrectPasswordExpectToThrowError() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user", "12345678");
        final User user = generateUser();

        when(userService.findByUsernameIsOrEmailIs(request.login(), request.login())).thenReturn(Optional.of(user));
        when(userService.login(request, user)).thenThrow(new AuthException(ErrorCode.PASSWORD_INCORRECT, "Incorrect password"));
        assertThrows(AuthException.class, () -> authInteractor.login(request), "Incorrect password");
        verify(userService, times(1)).findByUsernameIsOrEmailIs(request.login(), request.login());
        verify(userService, times(1)).login(request, user);
    }

    //endregion

    //region registration

    @Test
    void whenRegistrationExpectToSucceed() {
        final RegistrationRequest request = generateRegistrationRequest();
        final User user = generateUser();
        final UserEvent userEvent = generateUserEvent();
        final AuthorizationResponse response = new AuthorizationResponse(null, null);

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userService.save(user)).thenReturn(user);
        when(userMapper.toEvent(user)).thenReturn(userEvent);
        assertEquals(response, authInteractor.registration(request));
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
        verify(userMapper, times(1)).toEntity(request);
        verify(authServiceProducer, times(1)).send(userEvent);
        verify(userService, times(1)).save(user);
    }

    @Test
    void whenRegistrationAndArtistParameterIsTrueExpectToSucceed() {
        final RegistrationRequest request = generateRegistrationRequest();
        final User user = generateUser().toBuilder().roles(Set.of(Role.ROLE_USER, Role.ROLE_ARTIST)).build();
        final UserEvent userEvent = generateUserEvent();
        final AuthorizationResponse response = new AuthorizationResponse(null, null);

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userService.save(user)).thenReturn(user);
        when(userMapper.toEvent(user)).thenReturn(userEvent);
        assertEquals(response, authInteractor.registration(request));
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
        verify(userMapper, times(1)).toEntity(request);
        verify(authServiceProducer, times(1)).send(userEvent);
        verify(userService, times(1)).save(user);
    }

    @Test
    void whenRegistrationWithAlreadyExistUsernameExpectToThrowError() {
        final RegistrationRequest request = generateRegistrationRequest();
        final User user = generateUser();

        when(userService.findByUsername(request.username())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> authInteractor.registration(request));
        verify(userService, times(1)).findByUsername(request.username());
    }

    @Test
    void whenRegistrationWithAlreadyExistEmailExpectToThrowError() {
        final RegistrationRequest request = generateRegistrationRequest();
        final User user = generateUser();

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> authInteractor.registration(request));
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
    }

    //endregion

    //region registration via google account

    @Test
    void whenRegistrationViaGoogleAccountExpectToSucceed() {
        final RegistrationRequest request = generateRegistrationRequestViaGoogleToken("token");
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));
        final User saveUser = user.toBuilder().password("12345678").provider("GOOGLE").enabled(true).token(null).build();
        when(userMapper.toEntity(request)).thenReturn(saveUser);
        when(userService.saveOauth2User(saveUser)).thenReturn(saveUser);
        when(userService.generateTokens(saveUser)).thenReturn(response);
        assertEquals(response, authInteractor.registration(request));
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
        verify(userMapper, times(1)).toEntity(request);
        verify(userService, times(1)).saveOauth2User(saveUser);
        verify(userService, times(1)).generateTokens(saveUser);
    }

    @Test
    void whenRegistrationViaGoogleAccountAndArtistParameterIsTrueExpectToSucceed() {
        final RegistrationRequest request = generateRegistrationRequestViaGoogleToken("token");
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").roles(Set.of(Role.ROLE_USER, Role.ROLE_ARTIST)).build();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));
        final User saveUser = user.toBuilder().password("12345678").provider("GOOGLE").enabled(true).token(null).build();
        when(userMapper.toEntity(request)).thenReturn(saveUser);
        when(userService.saveOauth2User(saveUser)).thenReturn(saveUser);
        when(userService.generateTokens(saveUser)).thenReturn(response);
        assertEquals(response, authInteractor.registration(request));
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
        verify(userMapper, times(1)).toEntity(request);
        verify(userService, times(1)).saveOauth2User(saveUser);
        verify(userService, times(1)).generateTokens(saveUser);
    }

    @Test
    void whenRegistrationViaGoogleAccountWithAlreadyExistUsernameExpectToThrowError() {
        final RegistrationRequest request = generateRegistrationRequestViaGoogleToken("token");
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userService.findByUsername(request.username())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> authInteractor.registration(request), "User with this username was already exist");
        verify(userService, times(1)).findByUsername(request.username());
    }

    @Test
    void whenRegistrationViaGoogleAccountWithNullTokenExpectToThrowError() {
        final RegistrationRequest request = generateRegistrationRequestViaGoogleToken(null);
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> authInteractor.registration(request), "Token is not valid");
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
    }

    @Test
    void whenRegistrationViaGoogleAccountWithNotValidTokenExpectToThrowError() {
        final RegistrationRequest request = generateRegistrationRequestViaGoogleToken("token");
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("not-valid-token").build();

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> authInteractor.registration(request), "Token is not valid");
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
    }

    @Test
    void whenRegistrationViaGoogleAccountWithExpiredTokenExpectToThrowError() {
        final RegistrationRequest request = generateRegistrationRequestViaGoogleToken("token");
        final User user = generateUser().toBuilder().updatedAt(Instant.now().minus(2, ChronoUnit.HOURS)).provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userService.findByUsername(request.username())).thenReturn(Optional.empty());
        when(userService.findByEmail(request.email())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> authInteractor.registration(request), "Token is expire");
        verify(userService, times(1)).findByUsername(request.username());
        verify(userService, times(1)).findByEmail(request.email());
    }

    //endregion

    //region forgot password

    @Test
    void whenForgotPasswordWithCorrectDataAndLoginIsEmailExpectToSucceed() {
        final User user = generateUser();
        final String login = user.getEmail();
        final UserEvent userEvent = generateUserEvent();

        when(userService.forgotPassword(login)).thenReturn(user);
        when(userMapper.toEvent(user)).thenReturn(userEvent);
        assertDoesNotThrow(() -> authInteractor.forgotPassword(login));
        verify(userService, times(1)).forgotPassword(login);
        verify(userMapper, times(1)).toEvent(user);
        verify(authServiceProducer, times(1)).send(userEvent);
    }

    @Test
    void whenForgotPasswordWithCorrectDataAndLoginIsUsernameExpectToSucceed() {
        final User user = generateUser();
        final String login = user.getUsername();
        final UserEvent userEvent = generateUserEvent();

        when(userService.forgotPassword(login)).thenReturn(user);
        when(userMapper.toEvent(user)).thenReturn(userEvent);
        assertDoesNotThrow(() -> authInteractor.forgotPassword(login));
        verify(userService, times(1)).forgotPassword(login);
        verify(userMapper, times(1)).toEvent(user);
        verify(authServiceProducer, times(1)).send(userEvent);
    }

    @Test
    void whenForgotPasswordWithNotCorrectDataExpectToThrowError() {
        final User user = generateUser();
        final String login = user.getEmail();

        when(userService.forgotPassword(login)).thenThrow(new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        assertThrows(AuthException.class, () -> authInteractor.forgotPassword(login), "User is not exist");
        verify(userService, times(1)).forgotPassword(login);
    }

    //endregion

    //region reset password

    @Test
    void whenResetPasswordWithCorrectDataAndExpectToSucceed() {
        final User user = generateUser();
        final UserEvent userEvent = generateUserEvent();
        final ResetPasswordRequest request = new ResetPasswordRequest(user.getEmail(), user.getUuid(), "new password");
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(userService.resetPassword(request.email(), request.uuid(), request.password())).thenReturn(user);
        when(userMapper.toEvent(user)).thenReturn(userEvent);
        when(userService.generateTokens(user)).thenReturn(response);
        assertEquals(response, authInteractor.resetPassword(request));
        verify(userService, times(1)).resetPassword(request.email(), request.uuid(), request.password());
        verify(userMapper, times(1)).toEvent(user);
        verify(authServiceProducer, times(1)).send(userEvent);
        verify(userService, times(1)).generateTokens(user);
    }

    @Test
    void whenResetPasswordWithNotCorrectEmailExpectToThrowError() {
        final User user = generateUser();
        final ResetPasswordRequest request = new ResetPasswordRequest("email@gmail.com", user.getUuid(), "new password");

        when(userService.resetPassword(request.email(), request.uuid(), request.password()))
                .thenThrow(new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        assertThrows(AuthException.class, () -> authInteractor.resetPassword(request), "User is not exist");
        verify(userService, times(1)).resetPassword(request.email(), request.uuid(), request.password());
    }

    @Test
    void whenResetPasswordWithNotEnabledUserAccountExpectToThrowError() {
        final User user = generateUser().toBuilder().enabled(false).build();
        final ResetPasswordRequest request = new ResetPasswordRequest(user.getEmail(), user.getUuid(), "new password");

        when(userService.resetPassword(request.email(), request.uuid(), request.password()))
                .thenThrow(new AuthException(ErrorCode.USER_NOT_ENABLED, "User is not enabled"));
        assertThrows(AuthException.class, () -> authInteractor.resetPassword(request), "User is not enabled");
        verify(userService, times(1)).resetPassword(request.email(), request.uuid(), request.password());
    }

    @Test
    void whenResetPasswordWhenUUIDInDbIsNullExpectToThrowError() {
        final User user = generateUser().toBuilder().uuid(null).build();
        final ResetPasswordRequest request = new ResetPasswordRequest(user.getEmail(), UUID.randomUUID(), "new password");

        when(userService.resetPassword(request.email(), request.uuid(), request.password()))
                .thenThrow(new AuthException(ErrorCode.INCORRECT_SECURITY_DATA, "User did not request a password change request"));
        assertThrows(AuthException.class, () -> authInteractor.resetPassword(request), "User did not request a password change request");
        verify(userService, times(1)).resetPassword(request.email(), request.uuid(), request.password());
    }

    @Test
    void whenResetPasswordWithNotCorrectUUIDExpectToThrowError() {
        final User user = generateUser();
        final ResetPasswordRequest request = new ResetPasswordRequest(user.getEmail(), UUID.randomUUID(), "new password");

        when(userService.resetPassword(request.email(), request.uuid(), request.password()))
                .thenThrow(new AuthException(ErrorCode.INCORRECT_SECURITY_DATA, "Uuid is incorrect"));
        assertThrows(AuthException.class, () -> authInteractor.resetPassword(request), "Uuid is incorrect");
        verify(userService, times(1)).resetPassword(request.email(), request.uuid(), request.password());
    }

    //endregion

    //region refresh

    @Test
    void whenRefreshWithValidTokenExpectToSucceed() {
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(userService.refresh("token")).thenReturn(response);
        assertEquals(response, authInteractor.refresh("token"));
        verify(userService, times(1)).refresh("token");
    }

    @Test
    void whenRefreshWithInvalidTokenExpectToThrowError() {
        when(userService.refresh("token")).thenThrow(AuthException.class);
        assertThrows(AuthException.class, () -> authInteractor.refresh("token"));
        verify(userService, times(1)).refresh("token");
    }

    @Test
    void whenRefreshWithExpiredTokenExpectToThrowError() {
        when(userService.refresh("token")).thenThrow(ExpiredJwtException.class);
        assertThrows(ExpiredJwtException.class, () -> authInteractor.refresh("token"));
        verify(userService, times(1)).refresh("token");
    }

    //endregion

    //region validate

    @Test
    void whenValidateWithValidTokenExpectToSucceed() {
        validateToken(true);
    }

    @Test
    void whenValidateWithInvalidTokenExpectToUnsuccessful() {
        validateToken(false);
    }

    @Test
    void whenValidateWithExpiredTokenExpectToUnsuccessful() {
        validateToken(false);
    }

    void validateToken(boolean valid) {
        when(userService.validate("token")).thenReturn(valid);
        assertEquals(new TokenValidateResponse(valid), authInteractor.validate("token"));
        verify(userService, times(1)).validate("token");
    }

    //endregion

    //region activate

    @Test
    void whenActivateWithExistUUIDExpectToSucceed() {
        final User user = generateUser();
        final UserEvent userEvent = generateUserEvent();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");
        final RedirectView redirectView = new RedirectView();

        when(userService.activate(user.getUuid())).thenReturn(user);
        when(userMapper.toEvent(user)).thenReturn(userEvent);
        when(userService.generateTokens(user)).thenReturn(response);
        when(redirectService.redirect(response)).thenReturn(redirectView);
        assertEquals(redirectView, authInteractor.activate(user.getUuid()));
        verify(userService, times(1)).activate(user.getUuid());
        verify(userMapper, times(1)).toEvent(user);
        verify(authServiceProducer, times(1)).send(userEvent);
        verify(userService, times(1)).generateTokens(user);
        verify(redirectService, times(1)).redirect(response);
    }

    @Test
    void whenActivateWithNotExistUUIDExpectToThrowError() {
        final UUID uuid = generateUser().getUuid();

        when(userService.activate(uuid)).thenThrow(new AuthException(ErrorCode.USER_NOT_ENABLED, "User is not enabled"));
        assertThrows(AuthException.class, () -> authInteractor.activate(uuid), "User is not enabled");
        verify(userService, times(1)).activate(uuid);
    }

    @Test
    void whenActivateWithEnabledUserAccountExpectToThrowError() {
        final UUID uuid = generateUser().getUuid();

        when(userService.activate(uuid)).thenThrow(new AuthException(ErrorCode.USER_IS_ALREADY_ENABLED, "User is already enabled"));
        assertThrows(AuthException.class, () -> authInteractor.activate(uuid), "User is already enabled");
        verify(userService, times(1)).activate(uuid);
    }

    //endregion

    //region getUserViaEmailAndGoogleToken

    @Test
    void whenGetUserViaEmailAndGoogleTokenExpectToSucceed() {
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();
        final UserResponce userResponce = new UserResponce(user.getName(), user.getSurname(), user.getEmail());

        when(userService.findByEmailAndToken(user.getEmail(), user.getToken())).thenReturn(user);
        assertEquals(userResponce, authInteractor.getUserViaEmailAndGoogleToken(user.getEmail(), user.getToken()));
        verify(userService, times(1)).findByEmailAndToken(user.getEmail(), user.getToken());
    }

    @Test
    void whenGetUserViaEmailAndGoogleTokenWithNotCorrectDataExpectToThrowError() {
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userService.findByEmailAndToken(user.getEmail(), user.getToken())).thenThrow(new AuthException(ErrorCode.USER_NOT_FOUND, "User is not exist"));
        assertThrows(AuthException.class, () -> authInteractor.getUserViaEmailAndGoogleToken(user.getEmail(), user.getToken()), "User is not exist");
        verify(userService, times(1)).findByEmailAndToken(user.getEmail(), user.getToken());
    }

    @Test
    void whenGetUserViaEmailAndGoogleTokenWithExpiredTokenExpectToThrowError() {
        final User user = generateUser().toBuilder().updatedAt(Instant.now().minus(2, ChronoUnit.MINUTES)).provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userService.findByEmailAndToken(user.getEmail(), user.getToken())).thenReturn(user);
        assertThrows(AuthException.class, () -> authInteractor.getUserViaEmailAndGoogleToken(user.getEmail(), user.getToken()), "Token is expire");
        verify(userService, times(1)).findByEmailAndToken(user.getEmail(), user.getToken());
    }

    //endregion

    private static RegistrationRequest generateRegistrationRequest() {
        return new RegistrationRequest(
                "Bogdan",
                "Tkachuk",
                "pro100user",
                "pro100user@gmail.com",
                "12345678",
                false,
                null
        );
    }

    private static RegistrationRequest generateRegistrationRequestAndArtistIsTrue() {
        return new RegistrationRequest(
                "Bogdan",
                "Tkachuk",
                "pro100user",
                "pro100user@gmail.com",
                "12345678",
                true,
                null
        );
    }

    private static RegistrationRequest generateRegistrationRequestViaGoogleToken(String token) {
        return new RegistrationRequest(
                "Bogdan",
                "Tkachuk",
                "pro100user",
                "pro100user@gmail.com",
                "12345678",
                false,
                getOrNull(token)
        );
    }

    private static User generateUser() {
        return new User(
                1L,
                "Bogdan",
                "Tkachuk",
                "pro100user",
                "pro100user@gmail.com",
                "avatar",
                "$2a$10$V9vhbBR/SWyRrReXaYjMFuZtdnzqmtX8oGCXZKZVQtdydYQKKMGKu",
                Instant.now(),
                Instant.now(),
                "MORDENT",
                true,
                UUID.randomUUID(),
                null,
                Set.of(Role.ROLE_USER)
        );
    }

    private static UserEvent generateUserEvent() {
        return new UserEvent(
                1L,
                "Bogdan",
                "Tkachuk",
                "pro100user",
                "pro100user@gmail.com",
                Instant.now(),
                Instant.now(),
                true,
                UUID.randomUUID()
        );
    }
}