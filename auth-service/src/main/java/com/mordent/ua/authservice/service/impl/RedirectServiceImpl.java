package com.mordent.ua.authservice.service.impl;

import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import com.mordent.ua.authservice.security.SecurityAppConfig;
import com.mordent.ua.authservice.service.RedirectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectServiceImpl implements RedirectService {

    private final SecurityAppConfig securityAppConfig;

    @Override
    public RedirectView redirect(final AuthorizationResponse authorizationResponse) {
        final String uri = UriComponentsBuilder.fromUriString(securityAppConfig.activate().redirectUri())
                .queryParam("accessToken", authorizationResponse.accessToken())
                .queryParam("refreshToken", authorizationResponse.refreshToken())
                .build().toUriString();
        return new RedirectView(uri);
    }
}
