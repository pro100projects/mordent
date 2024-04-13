package com.mordent.ua.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.request.RegistrationRequest;
import com.mordent.ua.authservice.model.body.request.ResetPasswordRequest;
import com.mordent.ua.authservice.model.entity.Role;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.model.exception.AuthException;
import com.mordent.ua.authservice.model.exception.ErrorCategory;
import com.mordent.ua.authservice.model.exception.ErrorCode;
import com.mordent.ua.authservice.model.exception.ErrorResponse;
import com.mordent.ua.authservice.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@TestPropertySource("classpath:application.yaml")
class AuthControllerTest {

    private static final PostgreSQLContainer<?> postgresContainer;
    private static final KafkaContainer kafkaContainer;

    static {
        postgresContainer = (PostgreSQLContainer<?>) new PostgreSQLContainer("postgres:latest")
                .withDatabaseName("mordent")
                .withUsername("postgres")
                .withPassword("postgres")
                .withReuse(true);
        postgresContainer.start();

        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
        kafkaContainer.start();
    }

    @DynamicPropertySource
    private static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", postgresContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgresContainer::getUsername);
        registry.add("spring.flyway.password", postgresContainer::getPassword);
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    private final MockMvc mvc;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthControllerTest(MockMvc mvc, JwtProvider jwtProvider, ObjectMapper objectMapper) {
        this.mvc = mvc;
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    //region login

    @Test
    void whenLoginWithCorrectDataExpectToSucceed() throws Exception {
        final var request = new AuthorizationRequest("pro100user@gmail.com", "qWER1234!");

        mvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").isString())
                .andExpect(jsonPath("refreshToken").isString());
    }

    @Test
    void whenLoginWithoutFilledFieldExpectToThrowError() throws Exception {
        final var request = new AuthorizationRequest("", "qWER1234!");

        ErrorResponse errorResponse = objectMapper.readValue(
                mvc.perform(post("/api/auth/login")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("error").exists())
                        .andReturn().getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertNotNull(errorResponse);
        assertEquals(ErrorCategory.AUTH.getCode() + ErrorCode.VALIDATION_ERROR.getCode(), errorResponse.error().code());
        assertEquals("{\"login\":\"Login cannot be blank\"}", errorResponse.error().description());
    }

    @Test
    void whenLoginWithNotExistLoginExpectToThrowError() throws Exception {
        final var request = new AuthorizationRequest("pro100user18@gmail.com", "qWER1234!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("User is not exist", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/disable-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/enable-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenLoginWithNotEnabledUserAccountExpectToThrowError() throws Exception {
        final var request = new AuthorizationRequest("pro100user@gmail.com", "qWER123456!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_ENABLED, exception.getErrorCode());
        assertEquals("User is not enabled", exception.getMessage());
    }

    @Test
    void whenLoginWithWrongPasswordExpectToThrowError() throws Exception {
        final var request = new AuthorizationRequest("pro100user@gmail.com", "qWER123456!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.PASSWORD_INCORRECT, exception.getErrorCode());
        assertEquals("Incorrect password", exception.getMessage());
    }

    //endregion

    //region registration

    @Test
    @Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenRegistrationWithCorrectDataExpectToSucceed() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "pro100user1", "pro100user1@gmail.com", "qWER1234!", false, null);

        mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").doesNotExist())
                .andExpect(jsonPath("refreshToken").doesNotExist());
    }

    @Test
    void whenRegistrationWithoutFilledFieldExpectToThrowError() throws Exception {
        final var request = new RegistrationRequest("", "Tkachuk", "pro100user", "pro100user@gmail.com", "qWER1234!", false, null);

        final ErrorResponse errorResponse = objectMapper.readValue(
                mvc.perform(post("/api/auth/registration")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("error").exists())
                        .andReturn().getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertNotNull(errorResponse);
        assertEquals(ErrorCategory.AUTH.getCode() + ErrorCode.VALIDATION_ERROR.getCode(), errorResponse.error().code());
        assertEquals("{\"name\":\"Name cannot be blank\"}", errorResponse.error().description());
    }

    @Test
    void whenRegistrationWithAlreadyExistUsernameExpectToThrowError() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "pro100user", "pro100user1@gmail.com", "qWER1234!", false, null);

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_USERNAME_EXIST, exception.getErrorCode());
        assertEquals("User with this username was already exist", exception.getMessage());
    }

    @Test
    void whenRegistrationWithAlreadyExistEmailExpectToThrowError() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "pro100user1", "pro100user@gmail.com", "qWER1234!", false, null);

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_EMAIL_EXIST, exception.getErrorCode());
        assertEquals("User with this email was already exist", exception.getMessage());
    }

    //endregion

    //region registration via google account

    @Test
    @Sql(scripts = "/sql/disable-google-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenRegistrationViaGoogleAccountWithCorrectDataExpectToSucceed() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "bogdan.tkachuk", "bogdan.tkachuk@gmail.com", "qWER1234!", false, "token");

        mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").isString())
                .andExpect(jsonPath("refreshToken").isString());
    }

    @Test
    @Sql(scripts = "/sql/disable-google-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenRegistrationViaGoogleAccountWithNullTokenExpectToThrowError() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "bogdan.tkachuk", "bogdan.tkachuk@gmail.com", "qWER1234!", false, null);

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.TOKEN_NOT_VALID, exception.getErrorCode());
        assertEquals("Token is not valid", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/disable-google-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenRegistrationViaGoogleAccountWithNotValidTokenExpectToThrowError() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "bogdan.tkachuk", "bogdan.tkachuk@gmail.com", "qWER1234!", false, "not-valid-token");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.TOKEN_NOT_VALID, exception.getErrorCode());
        assertEquals("Token is not valid", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/disable-google-user-with-expired-token.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenRegistrationViaGoogleAccountWithExpiredTokenExpectToThrowError() throws Exception {
        final var request = new RegistrationRequest("Bogdan", "Tkachuk", "bogdan.tkachuk", "bogdan.tkachuk@gmail.com", "qWER1234!", false, "token");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/registration")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.TOKEN_NOT_VALID, exception.getErrorCode());
        assertEquals("Token is expire", exception.getMessage());
    }

    //endregion

    //region forgot password

    @Test
    void whenForgotPasswordWithCorrectDataAndLoginIsEmailExpectToSucceed() throws Exception {
        final var login = generateUser().getEmail();

        mvc.perform(post("/api/auth/forgot-password")
                        .queryParam("login", login)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenForgotPasswordWithCorrectDataAndLoginIsUsernameExpectToSucceed() throws Exception {
        final var login = generateUser().getUsername();

        mvc.perform(post("/api/auth/forgot-password")
                        .queryParam("login", login)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenForgotPasswordWithNotCorrectDataExpectToThrowError() throws Exception {
        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/forgot-password")
                        .queryParam("login", "email")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("User is not exist", exception.getMessage());
    }

    //endregion

    //region reset password

    @Test
    @Sql(scripts = "/sql/reset-password-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/reset-password-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenResetPasswordWithCorrectDataExpectToSucceed() throws Exception {
        final var user = generateUser().toBuilder().uuid(generateUUID()).build();
        final var request = new ResetPasswordRequest(user.getEmail(), user.getUuid(), "qWER1234!");

        mvc.perform(post("/api/auth/reset-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").isString())
                .andExpect(jsonPath("refreshToken").isString());
    }

    @Test
    void whenResetPasswordWithoutFilledFieldExpectToThrowError() throws Exception {
        final var request = new ResetPasswordRequest("", UUID.randomUUID(), "qWER1234!");

        final ErrorResponse errorResponse = objectMapper.readValue(
                mvc.perform(post("/api/auth/reset-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResponse().getContentAsString(),
                ErrorResponse.class
        );

        assertNotNull(errorResponse);
        assertEquals(ErrorCategory.AUTH.getCode() + ErrorCode.VALIDATION_ERROR.getCode(), errorResponse.error().code());
        assertEquals("{\"email\":\"Email cannot be blank\"}", errorResponse.error().description());
    }

    @Test
    void whenResetPasswordWithNotCorrectEmailExpectToThrowError() throws Exception {
        final var request = new ResetPasswordRequest("email@gmail.com", UUID.randomUUID(), "qWER1234!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/reset-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("User is not exist", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/disable-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/enable-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenResetPasswordWithNotEnabledUserAccountExpectToThrowError() throws Exception {
        final var user = generateUser().toBuilder().uuid(generateUUID()).build();
        final var request = new ResetPasswordRequest(user.getEmail(), user.getUuid(), "qWER1234!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/reset-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_ENABLED, exception.getErrorCode());
        assertEquals("User is not enabled", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/reset-password-after.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenResetPasswordWhenUUIDInDbIsNullExpectToThrowError() throws Exception {
        final var user = generateUser().toBuilder().uuid(generateUUID()).build();
        final var request = new ResetPasswordRequest(user.getEmail(), user.getUuid(), "qWER1234!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/reset-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.INCORRECT_SECURITY_DATA, exception.getErrorCode());
        assertEquals("User did not request a password change request", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/reset-password-before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/reset-password-after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenResetPasswordWithNotCorrectUUIDExpectToThrowError() throws Exception {
        final var user = generateUser();
        final var request = new ResetPasswordRequest(user.getEmail(), UUID.randomUUID(), "qWER1234!");

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/reset-password")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.INCORRECT_SECURITY_DATA, exception.getErrorCode());
        assertEquals("Uuid is incorrect", exception.getMessage());
    }

    //endregion

    //region refresh

    @Test
    void whenRefreshWithCorrectTokenExpectToSucceed() throws Exception {
        final var token = jwtProvider.generateTokens(generateUser()).refreshToken();

        mvc.perform(post("/api/auth/refresh")
                        .queryParam("token", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("accessToken").isString())
                .andExpect(jsonPath("refreshToken").isString());
    }

    @Test
    void whenRefreshWithoutTokenExpectToThrowError() throws Exception {
        final MissingServletRequestParameterException exception = (MissingServletRequestParameterException) mvc.perform(post("/api/auth/refresh"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals("Required request parameter 'token' for method parameter type String is not present", exception.getMessage());
    }

    @Test
    void whenRefreshWithWrongTokenExpectToThrowError() throws Exception {
        final var token = jwtProvider.generateTokens(
                generateUser().toBuilder()
                        .email("pro100user12345@gmail.com")
                        .build()
        ).refreshToken();

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/refresh")
                        .queryParam("token", "Bearer" + token)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.TOKEN_NOT_VALID, exception.getErrorCode());
        assertEquals("Token is not valid", exception.getMessage());
    }

    @Test
    void whenRefreshWithWrongUserIdInTokenExpectToThrowError() throws Exception {
        final var token = jwtProvider.generateTokens(
                generateUser().toBuilder()
                        .id(365L)
                        .build()
        ).refreshToken();

        final AuthException exception = (AuthException) mvc.perform(post("/api/auth/refresh")
                        .queryParam("token", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("User is not exist", exception.getMessage());
    }

    //endregion

    //region validate

    @Test
    void whenValidateWithCorrectTokenExpectToSucceed() throws Exception {
        final var token = jwtProvider.generateTokens(generateUser()).refreshToken();

        mvc.perform(post("/api/auth/token")
                        .queryParam("token", "Bearer " + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("validated").isBoolean())
                .andExpect(jsonPath("validated").value(true));
    }

    @Test
    void whenValidateWithoutTokenExpectToThrowError() throws Exception {
        final MissingServletRequestParameterException exception = (MissingServletRequestParameterException) mvc.perform(post("/api/auth/token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals("Required request parameter 'token' for method parameter type String is not present", exception.getMessage());
    }

    @Test
    void whenValidateWithWrongStartTokenExpectToUnsuccessful() throws Exception {
        final var token = jwtProvider.generateTokens(generateUser()).refreshToken();

        mvc.perform(post("/api/auth/token")
                        .queryParam("token", "Bearer" + token)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("validated").isBoolean())
                .andExpect(jsonPath("validated").value(false));
    }

    @Test
    void whenValidateWithWrongTokenExpectToUnsuccessful() throws Exception {
        final var token = jwtProvider.generateTokens(generateUser()).refreshToken();

        mvc.perform(post("/api/auth/token")
                        .queryParam("token", "Bearer " + token + "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("validated").isBoolean())
                .andExpect(jsonPath("validated").value(false));
    }

    //endregion

    //region activate

    @Test
    @Sql(scripts = "/sql/disable-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenActivateWithCorrectUUIDExpectToSucceed() throws Exception {
        final var uuid = generateUUID();

        mvc.perform(get("/api/auth/activate")
                        .queryParam("uuid", String.valueOf(uuid))
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void whenActivateWithoutUUIDExpectToThrowError() throws Exception {
        final MissingServletRequestParameterException exception = (MissingServletRequestParameterException) mvc.perform(get("/api/auth/activate"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals("Required request parameter 'uuid' for method parameter type UUID is not present", exception.getMessage());
    }

    @Test
    void whenActivateWithEnabledUserExpectToThrowError() throws Exception {
        final AuthException exception = (AuthException) mvc.perform(get("/api/auth/activate")
                        .queryParam("uuid", String.valueOf(UUID.randomUUID())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        assertEquals("User is not exist", exception.getMessage());
    }

    //endregion

    //region user

    @Test
    @Sql(scripts = "/sql/disable-google-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenGetUserWithCorrectDataExpectToSucceed() throws Exception {
        final var user = generateOauth2User();

        mvc.perform(get("/api/auth/user")
                        .queryParam("email", user.getEmail())
                        .queryParam("token", user.getToken())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name").value(user.getName()))
                .andExpect(jsonPath("surname").value(user.getSurname()))
                .andExpect(jsonPath("email").value(user.getEmail()));
    }

    @Test
    void whenGetUserWithoutEmailExpectToThrowError() throws Exception {
        final MissingServletRequestParameterException exception = (MissingServletRequestParameterException) mvc.perform(get("/api/auth/user"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals("Required request parameter 'email' for method parameter type String is not present", exception.getMessage());
    }

    @Test
    void whenGetUserWithoutTokenExpectToThrowError() throws Exception {
        final MissingServletRequestParameterException exception = (MissingServletRequestParameterException) mvc.perform(get("/api/auth/user")
                        .queryParam("email", "email")
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals("Required request parameter 'token' for method parameter type String is not present", exception.getMessage());
    }

    @Test
    @Sql(scripts = "/sql/disable-google-user-with-expired-token.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void whenGetUserWithExpiredTokenExpectToThrowError() throws Exception {
        final var user = generateOauth2User();

        final AuthException exception = (AuthException) mvc.perform(get("/api/auth/user")
                        .queryParam("email", user.getEmail())
                        .queryParam("token", user.getToken())
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("error").exists())
                .andReturn().getResolvedException();

        assertNotNull(exception);
        assertEquals(ErrorCode.TOKEN_NOT_VALID, exception.getErrorCode());
        assertEquals("Token is expire", exception.getMessage());
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
                null,
                null,
                Set.of(Role.ROLE_USER)
        );
    }

    private static User generateOauth2User() {
        return new User(
                2L,
                "Bogdan",
                "Tkachuk",
                "bogdan.tkachuk@gmail.com",
                "bogdan.tkachuk@gmail.com",
                "avatar",
                "password",
                Instant.now(),
                Instant.now(),
                "GOOGLE_NOT_ACTIVATE",
                false,
                null,
                "token",
                Set.of(Role.ROLE_USER)
        );
    }

    private static UUID generateUUID() {
        return UUID.fromString("a1437d4b-1bc0-44cf-8e5d-3e4462d543e1");
    }
}
