package com.mordent.ua.authservice.service;

import com.mordent.ua.authservice.model.body.response.AuthorizationResponse;
import org.springframework.web.servlet.view.RedirectView;

public interface RedirectService {

    RedirectView redirect(AuthorizationResponse authorizationResponse);
}
