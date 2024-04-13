package com.mordent.ua.authservice.security.oauth2;

import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.model.entity.User;
import com.mordent.ua.authservice.security.JwtProvider;
import com.mordent.ua.authservice.security.SecurityAppConfig;
import com.mordent.ua.authservice.service.UserService;
import com.mordent.ua.authservice.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final SecurityAppConfig securityAppConfig;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("handle authentication via oauth2");
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        DefaultOidcUser defaultOidcUser = ((DefaultOidcUser) authentication.getPrincipal());

        User user = userService.findByEmail(defaultOidcUser.getEmail()).orElse(null);
        if (user == null) {
            log.info("save oauth2 user");
            user = User.getGoogleUser(defaultOidcUser);
        }
        if (user.getProvider().equals("GOOGLE_NOT_ACTIVATE") && !user.isEnabled()) {
            log.info("update oauth2 user token");
            user = user.toBuilder().token(defaultOidcUser.getIdToken().getTokenValue()).build();
            user = userService.saveOauth2User(user);

            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("email", user.getEmail())
                    .queryParam("token", user.getToken())
                    .build().toUriString();
        }
        log.info("oauth2 login is success");

        AuthorizationResponse authorizationResponse = jwtProvider.generateTokens(user);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", authorizationResponse.accessToken())
                .queryParam("refreshToken", authorizationResponse.refreshToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return securityAppConfig.oauth2().authorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equals(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()
                            && authorizedURI.getPath().equals(clientRedirectUri.getPath());
                });
    }
}
