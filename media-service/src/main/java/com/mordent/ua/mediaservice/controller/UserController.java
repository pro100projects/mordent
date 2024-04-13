package com.mordent.ua.mediaservice.controller;

import com.mordent.ua.mediaservice.interactor.UserInteractor;
import com.mordent.ua.mediaservice.model.body.request.UserPasswordUpdateRequest;
import com.mordent.ua.mediaservice.model.body.request.UserUpdateRequest;
import com.mordent.ua.mediaservice.model.body.response.UserAvatarResponse;
import com.mordent.ua.mediaservice.model.body.response.UserResponse;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserInteractor userInteractor;

    @GetMapping
    public Mono<UserResponse> getUserInfo(
            @AuthenticationPrincipal UserSecurity userSecurity
    ) {
        return userInteractor.findById(userSecurity.id());
    }

    @PutMapping
    public Mono<UserResponse> update(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        request = request.toBuilder().id(userSecurity.id()).build();
        return userInteractor.update(request).map(Tuple2::getT1);
    }

    @PutMapping("avatar")
    public Mono<UserAvatarResponse> update(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestPart(name = "avatar") FilePart avatar
    ) {
        return userInteractor.updateAvatar(userSecurity.id(), avatar);
    }

    @PutMapping("password")
    public Mono<Void> updatePassword(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestBody UserPasswordUpdateRequest request
    ) {
        request = request.toBuilder().id(userSecurity.id()).build();
        return userInteractor.updatePassword(request);
    }

    @DeleteMapping
    public Mono<Void> delete(
            @AuthenticationPrincipal UserSecurity userSecurity
    ) {
        return userInteractor.delete(userSecurity.id());
    }

    @DeleteMapping("avatar")
    public Mono<Void> deleteAvatar(
            @AuthenticationPrincipal UserSecurity userSecurity
    ) {
        return userInteractor.deleteAvatar(userSecurity.id());
    }
}
