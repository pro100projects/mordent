package com.mordent.ua.mediaservice.interactor.impl;

import com.mordent.ua.mediaservice.interactor.UserInteractor;
import com.mordent.ua.mediaservice.mapper.UserMapper;
import com.mordent.ua.mediaservice.model.body.request.UserPasswordUpdateRequest;
import com.mordent.ua.mediaservice.model.body.request.UserUpdateRequest;
import com.mordent.ua.mediaservice.model.body.response.UserAvatarResponse;
import com.mordent.ua.mediaservice.model.body.response.UserResponse;
import com.mordent.ua.mediaservice.service.FileService;
import com.mordent.ua.mediaservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserInteractorImpl implements UserInteractor {

    private final UserMapper userMapper;
    private final UserService userService;
    private final FileService fileService;

    @Override
    public Mono<UserResponse> findById(final Long userId) {
        return userService.findById(userId)
                .map(userMapper::toDomainModel)
                .map(userMapper::toUserResponse);
    }

    @Override
    public Mono<Tuple2<UserResponse, Boolean>> update(final UserUpdateRequest request) {
        return userService.update(userMapper.toDataModel(request))
                .map(tuple -> tuple.mapT1(userMapper::toDomainModel))
                .map(tuple -> tuple.mapT1(userMapper::toUserResponse));
    }

    @Override
    public Mono<UserAvatarResponse> updateAvatar(final Long userId, final FilePart avatar) {
        return userService.findById(userId)
                .map(userMapper::toDomainModel)
                .flatMap(user -> fileService.updateUserAvatar(user, avatar))
                .flatMap(s -> userService.updateAvatar(userId, avatar.filename()))
                .map(userMapper::toDomainModel)
                .map(userMapper::toUserAvatarResponse);
    }

    @Override
    public Mono<Void> updatePassword(final UserPasswordUpdateRequest request) {
        return userService.updatePassword(request.id(), request.oldPassword(), request.newPassword());
    }

    @Override
    public Mono<Void> delete(final Long userId) {
        return userService.delete(userId);
    }

    @Override
    public Mono<Void> deleteAvatar(final Long userId) {
        return userService.findById(userId)
                .map(user -> user.toBuilder().avatar(null).build())
                .flatMap(userService::update)
                .map(Tuple2::getT1)
                .map(userMapper::toDomainModel)
                .flatMap(user -> fileService.updateUserAvatar(user, null))
                .then();
    }
}
