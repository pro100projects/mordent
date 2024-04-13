package com.mordent.ua.mediaservice.mapper;

import com.mordent.ua.mediaservice.model.body.request.UserUpdateRequest;
import com.mordent.ua.mediaservice.model.body.response.UserAvatarResponse;
import com.mordent.ua.mediaservice.model.body.response.UserResponse;
import com.mordent.ua.mediaservice.model.data.User;
import com.mordent.ua.mediaservice.model.data.UserSecurity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User toDataModel(UserUpdateRequest request);

    com.mordent.ua.mediaservice.model.domain.User toDomainModel(User user);

    com.mordent.ua.mediaservice.model.domain.UserSecurity toDomainModel(UserSecurity userSecurity);

    UserAvatarResponse toUserAvatarResponse(com.mordent.ua.mediaservice.model.domain.User user);

    UserResponse toUserResponse(com.mordent.ua.mediaservice.model.domain.User user);
}
