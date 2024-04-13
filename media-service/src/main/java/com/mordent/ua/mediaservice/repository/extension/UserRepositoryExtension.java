package com.mordent.ua.mediaservice.repository.extension;

import com.mordent.ua.mediaservice.model.data.UserSecurity;
import reactor.core.publisher.Mono;

public interface UserRepositoryExtension {

    Mono<UserSecurity> findByUserId(Long userId);
}
