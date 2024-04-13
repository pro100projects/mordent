package com.mordent.ua.mediaservice.repository;

import com.mordent.ua.mediaservice.model.data.User;
import com.mordent.ua.mediaservice.repository.extension.UserRepositoryExtension;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long>, UserRepositoryExtension {

    Mono<User> findByUsername(String username);
}
