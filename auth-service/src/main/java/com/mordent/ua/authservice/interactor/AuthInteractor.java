package com.mordent.ua.authservice.interactor;

import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.request.RegistrationRequest;
import com.mordent.ua.authservice.model.body.request.ResetPasswordRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.body.response.TokenValidateResponse;
import com.mordent.ua.authservice.model.body.response.UserResponce;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

public interface AuthInteractor {

    AuthorizationResponse login(AuthorizationRequest request);

    AuthorizationResponse registration(RegistrationRequest request);

    void forgotPassword(String login);

    AuthorizationResponse resetPassword(ResetPasswordRequest request);

    AuthorizationResponse refresh(String token);

    TokenValidateResponse validate(String token);

    RedirectView activate(UUID uuid);

    UserResponce getUserViaEmailAndGoogleToken(String email, String token);

    RedirectView redirectToOauth2(String requestUri, String redirectUri);
}
