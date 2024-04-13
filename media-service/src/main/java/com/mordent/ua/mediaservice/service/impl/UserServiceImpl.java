package com.mordent.ua.mediaservice.service.impl;

import com.mordent.ua.mediaservice.model.data.User;
import com.mordent.ua.mediaservice.model.data.UserSecurity;
import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import com.mordent.ua.mediaservice.repository.UserRepository;
import com.mordent.ua.mediaservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> findById(final Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new MediaException(ErrorCode.USER_NOT_FOUND, "User is no exist")));
    }

    @Override
    public Mono<UserSecurity> findByUserId(final Long userId) {
        return userRepository.findByUserId(userId)
                .switchIfEmpty(Mono.error(new MediaException(ErrorCode.USER_NOT_FOUND, "User is not exist")));
    }

    @Override
    public Mono<Tuple2<User, Boolean>> update(final User newUser) {
        return findById(newUser.id())
                .flatMap(oldUser -> {
                    if (!oldUser.username().equals(newUser.username())) {
                        return userRepository.findByUsername(newUser.username())
                                .flatMap(user -> {
                                    if (user == null) return Mono.just(oldUser);
                                    return Mono.error(new MediaException(ErrorCode.USER_USERNAME_EXIST, "User with this username was already exist"));
                                })
                                .defaultIfEmpty(oldUser);
                    }
                    return Mono.just(oldUser);
                })
                .flatMap(oldUser -> Mono.zip(
                        Mono.just(oldUser).map(user -> user.overwritingVariables(newUser)).flatMap(userRepository::save),
                        Mono.just(oldUser).map(user -> !user.username().equals(newUser.username()))
                ));
    }

    @Override
    public Mono<User> updateAvatar(final Long userId, final String avatar) {
        return findById(userId)
                .map(user -> user.toBuilder().avatar(avatar).build())
                .flatMap(userRepository::save);
    }

    @Override
    public Mono<Void> updatePassword(final Long userId, final String oldPassword, final String newPassword) {
        return findById(userId)
                .flatMap(user -> {
                    if(!passwordEncoder.matches(oldPassword, user.password())) {
                        return Mono.error(new MediaException(ErrorCode.USER_PASSWORD_INVALID, "Invalid password"));
                    }
                    return Mono.just(user.toBuilder().password(passwordEncoder.encode(newPassword)).build());
                })
                .flatMap(userRepository::save)
                .doOnSuccess(x -> log.info("User has successfully updated their password"))
                .then(Mono.empty());
    }

    @Override
    public Mono<Void> delete(final Long userId) {
        return Mono.error(new MediaException(ErrorCode.UNEXPECTED, "Method is no implement"));
    }
}
