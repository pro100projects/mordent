package com.mordent.ua.mediaservice.interactor;

import com.mordent.ua.mediaservice.model.body.request.UserPasswordUpdateRequest;
import com.mordent.ua.mediaservice.model.body.request.UserUpdateRequest;
import com.mordent.ua.mediaservice.model.body.response.UserAvatarResponse;
import com.mordent.ua.mediaservice.model.body.response.UserResponse;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface UserInteractor {

    Mono<UserResponse> findById(Long userId);

    Mono<Tuple2<UserResponse, Boolean>> update(UserUpdateRequest request);

    Mono<UserAvatarResponse> updateAvatar(Long userId, FilePart avatar);

    Mono<Void> updatePassword(UserPasswordUpdateRequest request);

    Mono<Void> delete(Long userId);

    Mono<Void> deleteAvatar(Long userId);
}
