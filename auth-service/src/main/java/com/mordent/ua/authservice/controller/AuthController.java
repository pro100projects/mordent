package com.mordent.ua.authservice.controller;

import com.mordent.ua.authservice.interactor.AuthInteractor;
import com.mordent.ua.authservice.model.body.request.AuthorizationRequest;
import com.mordent.ua.authservice.model.body.request.RegistrationRequest;
import com.mordent.ua.authservice.model.body.request.ResetPasswordRequest;
import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.body.response.TokenValidateResponse;
import com.mordent.ua.authservice.model.body.response.UserResponce;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthInteractor authInteractor;

    @PostMapping("login")
    public ResponseEntity<AuthorizationResponse> login(@Valid @RequestBody final AuthorizationRequest request) {
        return ResponseEntity.ok(authInteractor.login(request));
    }

    @PostMapping("registration")
    public ResponseEntity<AuthorizationResponse> registration(@Valid @RequestBody final RegistrationRequest request) {
        return ResponseEntity.ok(authInteractor.registration(request));
    }

    @PostMapping("forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam final String login) {
        authInteractor.forgotPassword(login);
        return ResponseEntity.ok().build();
    }

    @PostMapping("reset-password")
    public ResponseEntity<AuthorizationResponse> changePassword(@Valid @RequestBody final ResetPasswordRequest request) {
        return ResponseEntity.ok(authInteractor.resetPassword(request));
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthorizationResponse> refresh(@RequestParam final String token) {
        return ResponseEntity.ok(authInteractor.refresh(token));
    }

    @PostMapping("token")
    public ResponseEntity<TokenValidateResponse> validate(@RequestParam final String token) {
        return ResponseEntity.ok(authInteractor.validate(token));
    }

    @GetMapping("activate")
    public RedirectView activate(@RequestParam final UUID uuid) {
        return authInteractor.activate(uuid);
    }

    @GetMapping("user")
    public ResponseEntity<UserResponce> getUserViaEmailAndGoogleToken(@RequestParam final String email, @RequestParam final String token) {
        return ResponseEntity.ok(authInteractor.getUserViaEmailAndGoogleToken(email, token));
    }

    @GetMapping("oauth2")
    public RedirectView redirectToOauth2(final HttpServletRequest request, final @RequestParam(name = "redirect_uri") String redirectUri) {
        return authInteractor.redirectToOauth2(request.getRequestURL().toString(), redirectUri);
    }
}
