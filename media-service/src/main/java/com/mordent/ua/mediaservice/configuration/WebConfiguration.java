package com.mordent.ua.mediaservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.util.annotation.NonNull;

import java.util.Arrays;

@EnableWebFlux
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebFluxConfigurer {

    private final Environment env;

    @Override
    public void addCorsMappings(@NonNull final CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*").allowedMethods("*").allowedOrigins("*");
    }

    @Override
    public void addResourceHandlers(@NonNull final ResourceHandlerRegistry registry) {
        if (Arrays.stream(env.getActiveProfiles()).anyMatch(profile -> profile.equals("localDocker") || profile.equals("prod"))) {
            registry
                    .addResourceHandler("/files/avatars/**")
                    .addResourceLocations("file:/static/files/images/avatars/");
            registry
                    .addResourceHandler("/files/songs/**")
                    .addResourceLocations("file:/static/files/songs/");
            registry
                    .addResourceHandler("/files/images/**")
                    .addResourceLocations("file:/static/files/images/songs/");
            registry
                    .addResourceHandler("/files/albums/**")
                    .addResourceLocations("file:/static/files/images/albums/");
            registry
                    .addResourceHandler("/files/playlists/**")
                    .addResourceLocations("file:/static/files/images/playlists/");
        } else {
            registry
                    .addResourceHandler("/files/avatars/**")
                    .addResourceLocations("classpath:/static/files/images/avatars/");
            registry
                    .addResourceHandler("/files/songs/**")
                    .addResourceLocations("classpath:/static/files/songs/");
            registry
                    .addResourceHandler("/files/images/**")
                    .addResourceLocations("classpath:/static/files/images/songs/");
            registry
                    .addResourceHandler("/files/albums/**")
                    .addResourceLocations("classpath:/static/files/images/albums/");
            registry
                    .addResourceHandler("/files/playlists/**")
                    .addResourceLocations("classpath:/static/files/images/playlists/");
        }
    }

    //declared here to avoid bean looping
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
