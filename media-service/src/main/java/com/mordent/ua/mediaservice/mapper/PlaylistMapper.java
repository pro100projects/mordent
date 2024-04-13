package com.mordent.ua.mediaservice.mapper;

import com.mordent.ua.mediaservice.model.body.request.PlaylistRequest;
import com.mordent.ua.mediaservice.model.body.response.PlaylistResponse;
import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.Playlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper
public interface PlaylistMapper {

    @Mappings(value = {
            @Mapping(target = "createdAt", expression = "java(Instant.now())"),
            @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    })
    Playlist toDataModel(PlaylistRequest request);

    com.mordent.ua.mediaservice.model.domain.Playlist toDomainModel(Playlist playlist);

    @Mapping(target = "id", source = "playlist.id")
    com.mordent.ua.mediaservice.model.domain.Playlist toDomainModel(Playlist playlist, Like like, Set<Long> songIds);

    PlaylistResponse toResponseModel(com.mordent.ua.mediaservice.model.domain.Playlist playlist);
}
