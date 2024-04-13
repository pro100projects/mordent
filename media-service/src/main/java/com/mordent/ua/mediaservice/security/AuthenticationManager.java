package com.mordent.ua.mediaservice.security;

import com.mordent.ua.mediaservice.mapper.UserMapper;
import com.mordent.ua.mediaservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtFilter jwtFilter;
    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String token = authentication.getCredentials().toString();
        return jwtFilter.validateToken(token)
                .flatMap(valid -> {
                    if (valid) {
                        return jwtFilter.getUserIdFromToken(token)
                                .flatMap(userId -> userService.findByUserId(userId)
                                        .map(userMapper::toDomainModel)
                                        .map(userSecurity -> new UsernamePasswordAuthenticationToken(userSecurity, null, userSecurity.getAuthorities())));
                    } else {
                        return Mono.empty();
                    }
                });
    }
}
