package com.mordent.ua.mediaservice.configuration;

import com.mordent.ua.mediaservice.security.AuthenticationManager;
import com.mordent.ua.mediaservice.security.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import reactor.util.annotation.NonNull;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class WebSecurityConfiguration {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(@NonNull final ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/files/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/songs", "/api/albums", "/api/playlists").hasAnyRole("ARTIST", "ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/songs", "/api/albums", "/api/playlists").hasAnyRole("ARTIST", "ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/songs", "/api/albums", "/api/playlists").hasAnyRole("ARTIST", "ADMIN")
                .anyExchange().authenticated()
                .and()
                .build();
    }
}
