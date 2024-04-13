package com.mordent.ua.authservice.mapper;

import com.mordent.ua.authservice.kafka.event.UserEvent;
import com.mordent.ua.authservice.model.body.request.RegistrationRequest;
import com.mordent.ua.authservice.model.entity.Role;
import com.mordent.ua.authservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

@Mapper
public interface UserMapper {

    @Mapping(source = "artist", target = "roles", qualifiedByName = "mapUserRoles")
    User toEntity(RegistrationRequest request);

    UserEvent toEvent(User user);

    @Named("mapUserRoles")
    default Set<Role> mapUserRoles(boolean artist) {
        Set<Role> roles = new HashSet<>(Set.of(Role.ROLE_USER));
        if(artist) {
            roles.add(Role.ROLE_ARTIST);
        }
        return roles;
    }
}
