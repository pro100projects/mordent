package com.mordent.ua.mediaservice.mapper;

import com.mordent.ua.mediaservice.model.body.request.AlbumRequest;
import com.mordent.ua.mediaservice.model.body.response.AlbumResponse;
import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.model.data.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper
public interface AlbumMapper {

    @Mappings(value = {
            @Mapping(target = "createdAt", expression = "java(Instant.now())"),
            @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    })
    Album toDataModel(AlbumRequest request);

    com.mordent.ua.mediaservice.model.domain.Album toDomainModel(Album album);

    @Mapping(source = "album.id", target = "id")
    com.mordent.ua.mediaservice.model.domain.Album toDomainModel(Album album, Like like, Set<Long> songIds);

    AlbumResponse toResponseModel(com.mordent.ua.mediaservice.model.domain.Album album);
}
