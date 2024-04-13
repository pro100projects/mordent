package com.mordent.ua.mediaservice.service;

import com.mordent.ua.mediaservice.model.data.User;
import com.mordent.ua.mediaservice.model.data.UserSecurity;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface UserService {

    Mono<User> findById(Long userId);

    Mono<UserSecurity> findByUserId(Long userId);

    Mono<Tuple2<User, Boolean>> update(User user);

    Mono<User> updateAvatar(Long user, String avatar);

    Mono<Void> updatePassword(Long userId, String oldPassword, String newPassword);

    Mono<Void> delete(Long userId);
}
