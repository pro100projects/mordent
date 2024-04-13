package com.mordent.ua.authservice.service;

import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.entity.Role;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.repository.UserRepository;
import com.mordent.ua.authservice.security.JwtProvider;
import com.mordent.ua.authservice.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    //region login

    @Test
    void whenLoginWithCorrectDataExpectToSucceed() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user@gmail.com", "12345678");
        final User user = generateUser();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtProvider.generateTokens(user)).thenReturn(response);
        assertEquals(response, userService.login(request, user));
        verify(passwordEncoder, times(1)).matches(request.password(), user.getPassword());
        verify(jwtProvider, times(1)).generateTokens(user);
    }

    @Test
    void whenLoginWithNotEnabledUserAccountExpectToThrowError() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user@gmail.com", "1234567890");
        final User user = generateUser().toBuilder().enabled(false).build();

        assertThrows(AuthException.class, () -> userService.login(request, user), "User is not enabled");
    }

    @Test
    void whenLoginWithIncorrectPasswordExpectToThrowError() {
        final AuthorizationRequest request = new AuthorizationRequest("pro100user@gmail.com", "1234567890");
        final User user = generateUser();

        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);
        assertThrows(AuthException.class, () -> userService.login(request, user));
        verify(passwordEncoder, times(1)).matches(request.password(), user.getPassword());
        verify(jwtProvider, times(0)).generateTokens(user);
    }

    //endregion

    //region registration

    @Test
    void whenRegistrationWithCorrectDataExpectToSucceed() {
        final User user = generateUser();

        when(userRepository.save(user)).thenReturn(user);
        assertDoesNotThrow(() -> userService.save(user));
        verify(userRepository, times(1)).save(user);
    }

    //endregion

    //region registration via google account

    @Test
    void whenRegistrationViaGoogleAccountWithCorrectDataExpectToSucceed() {
        final User user = generateUser().toBuilder().provider("GOOGLE").build();

        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(userRepository.save(user)).thenReturn(user);
        assertDoesNotThrow(() -> userService.saveOauth2User(user));
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenRegistrationViaNotActivateGoogleAccountWithCorrectDataExpectToSucceed() {
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").build();

        when(userRepository.save(user)).thenReturn(user);
        assertDoesNotThrow(() -> userService.saveOauth2User(user));
        verify(passwordEncoder, times(0)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    //endregion

    //region forgot password

    @Test
    void whenForgotPasswordWithCorrectDataAndLoginIsEmailExpectToSucceed() {
        final User user = generateUser().toBuilder().uuid(null).build();
        final String login = user.getEmail();

        when(userRepository.findByUsernameIsOrEmailIs(login, login)).thenReturn(Optional.of(user));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);
        assertEquals(user, userService.forgotPassword(login));
        verify(userRepository, times(1)).findByUsernameIsOrEmailIs(login, login);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenForgotPasswordWithCorrectDataAndLoginIsUsernameExpectToSucceed() {
        final User user = generateUser().toBuilder().uuid(null).build();
        final String login = user.getUsername();

        when(userRepository.findByUsernameIsOrEmailIs(login, login)).thenReturn(Optional.of(user));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);
        assertEquals(user, userService.forgotPassword(login));
        verify(userRepository, times(1)).findByUsernameIsOrEmailIs(login, login);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenForgotPasswordWithNotCorrectDataExpectToThrowError() {
        final String login = "email";

        when(userRepository.findByUsernameIsOrEmailIs(login, login)).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> userService.forgotPassword(login), "User is not exist");
        verify(userRepository, times(1)).findByUsernameIsOrEmailIs(login, login);
    }

    //endregion

    //region reset password

    @Test
    void whenResetPasswordWithCorrectDataExpectToSucceed() {
        User user = generateUser();
        final String email = user.getEmail();
        final UUID uuid = user.getUuid();
        final String password = "new password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn(password);
        user = user.toBuilder().password(password).uuid(null).build();
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.resetPassword(email, uuid, password));
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenResetPasswordWithNotCorrectEmailExpectToThrowError() {
        final String email = "email@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> userService.resetPassword(email, UUID.randomUUID(), "new password"), "User is not exist");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenResetPasswordWithNotEnabledUserAccountExpectToThrowError() {
        final User user = generateUser().toBuilder().enabled(false).build();
        final String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> userService.resetPassword(email, user.getUuid(), "new password"), "User is not enabled");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenResetPasswordWhenUUIDInDbIsNullExpectToThrowError() {
        final User user = generateUser().toBuilder().uuid(null).build();
        final String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> userService.resetPassword(email, UUID.randomUUID(), "new password"), "User did not request a password change request");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void whenResetPasswordWithNotCorrectUUIDExpectToThrowError() {
        final User user = generateUser();
        final String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> userService.resetPassword(email, UUID.randomUUID(), "new password"), "Uuid is incorrect");
        verify(userRepository, times(1)).findByEmail(email);
    }

    //endregion

    //region generate tokens

    @Test
    void whenGenerateTokensExpectToSucceed() {
        final User user = generateUser();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(jwtProvider.generateTokens(user)).thenReturn(response);
        assertEquals(response, userService.generateTokens(user));
        verify(jwtProvider, times(1)).generateTokens(user);
    }

    //endregion

    //region refresh

    @Test
    void whenRefreshWithValidTokenExpectToSucceed() {
        final User user = generateUser();
        final AuthorizationResponse response = new AuthorizationResponse("accessToken", "refreshToken");

        when(jwtProvider.getUserIdFromToken("token")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtProvider.generateTokens(user)).thenReturn(response);
        assertEquals(response, userService.refresh("token"));
        verify(jwtProvider, times(1)).getUserIdFromToken("token");
        verify(userRepository, times(1)).findById(user.getId());
        verify(jwtProvider, times(1)).generateTokens(user);
    }

    @Test
    void whenRefreshWithInvalidTokenExpectToThrowError() {
        when(jwtProvider.getUserIdFromToken("token")).thenThrow(JwtException.class);
        assertThrows(JwtException.class, () -> userService.refresh("token"));
        verify(jwtProvider, times(1)).getUserIdFromToken("token");
    }

    @Test
    void whenRefreshWithInvalidEmailInTokenExpectToThrowError() {
        final User user = generateUser();

        when(jwtProvider.getUserIdFromToken("token")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> userService.refresh("token"));
        verify(jwtProvider, times(1)).getUserIdFromToken("token");
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void whenRefreshWithExpiredTokenExpectToThrowError() {
        when(jwtProvider.getUserIdFromToken("token")).thenThrow(ExpiredJwtException.class);
        assertThrows(ExpiredJwtException.class, () -> userService.refresh("token"));
        verify(jwtProvider, times(1)).getUserIdFromToken("token");
    }

    //endregion

    //region validate

    @Test
    void whenValidateWithValidTokenExpectToSucceed() {
        validateToken(true);
    }

    @Test
    void whenRefreshWithInvalidTokenExpectToUnsuccessful() {
        validateToken(false);
    }

    @Test
    void whenRefreshWithExpiredTokenExpectToUnsuccessful() {
        validateToken(false);
    }

    void validateToken(boolean valid) {
        when(jwtProvider.validateToken("token")).thenReturn(valid);
        assertEquals(valid, userService.validate("token"));
        verify(jwtProvider, times(1)).validateToken("token");
    }

    //endregion

    //region activate

    @Test
    void whenActivateWithExistUUIDExpectToSucceed() {
        User user = generateUser().toBuilder().enabled(false).build();
        final UUID uuid = user.getUuid();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        user = user.toBuilder().enabled(true).uuid(null).build();
        when(userRepository.save(user)).thenReturn(user);
        assertEquals(user, userService.activate(uuid));
        verify(userRepository, times(1)).findByUuid(uuid);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenActivateWithNotExistUUIDExpectToThrowError() {
        final UUID uuid = UUID.randomUUID();

        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> userService.activate(uuid), "User is not exist");
        verify(userRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void whenActivateWithEnabledUserAccountExpectToThrowError() {
        final User user = generateUser().toBuilder().enabled(true).build();

        when(userRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        assertThrows(AuthException.class, () -> userService.activate(user.getUuid()), "User is already enabled");
        verify(userRepository, times(1)).findByUuid(user.getUuid());
    }

    //endregion

    //region findByEmailAndToken

    @Test
    void whenFindByEmailAndTokenWithCorrectDataExpectToSucceed() {
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userRepository.findByEmailAndToken(user.getEmail(), user.getToken())).thenReturn(Optional.of(user));
        assertEquals(user, userService.findByEmailAndToken(user.getEmail(), user.getToken()));
        verify(userRepository, times(1)).findByEmailAndToken(user.getEmail(), user.getToken());
    }

    @Test
    void whenFindByEmailAndTokenWithNotCorrectDataExpectToThrowError() {
        final User user = generateUser().toBuilder().provider("GOOGLE_NOT_ACTIVATE").uuid(null).token("token").build();

        when(userRepository.findByEmailAndToken(user.getEmail(), user.getToken())).thenReturn(Optional.empty());
        assertThrows(AuthException.class, () -> userService.findByEmailAndToken(user.getEmail(), user.getToken()), "User is not exist");
        verify(userRepository, times(1)).findByEmailAndToken(user.getEmail(), user.getToken());
    }

    //endregion

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
}