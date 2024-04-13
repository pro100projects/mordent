package com.mordent.ua.mediaservice.mapper;

import com.mordent.ua.mediaservice.model.body.request.SongRequest;
import com.mordent.ua.mediaservice.model.body.response.ListenResponse;
import com.mordent.ua.mediaservice.model.body.response.SongResponse;
import com.mordent.ua.mediaservice.model.body.response.SongStatisticResponse;
import com.mordent.ua.mediaservice.model.body.response.SongWithMetadataResponse;
import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.model.data.Song;
import com.mordent.ua.mediaservice.model.data.User;
import com.mordent.ua.mediaservice.model.domain.SongStatistic;
import com.mordent.ua.mediaservice.model.domain.SongWithMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

@Mapper(uses = {UserMapper.class, AlbumMapper.class})
public interface SongMapper {

    @Mappings(value = {
            @Mapping(target = "playback", expression = "java(0L)")
    })
    Song toDataModel(SongRequest request);

    Song toDataModel(com.mordent.ua.mediaservice.model.domain.Song song);

    com.mordent.ua.mediaservice.model.domain.Song toDomainModel(Song song);

    SongResponse toResponseModel(com.mordent.ua.mediaservice.model.domain.Song song);

    @Mappings(value = {
            @Mapping(target = "id", source = "song.id"),
            @Mapping(target = "name", source = "song.name"),
            @Mapping(target = "user", source = "user", qualifiedByName = "toSongUser"),
            @Mapping(target = "album", source = "album", qualifiedByName = "toSongAlbum"),
            @Mapping(target = "imageFilename", source = "song.imageFilename"),
            @Mapping(target = "createdAt", source = "song.createdAt"),
            @Mapping(target = "updatedAt", source = "song.updatedAt"),
    })
    SongResponse toResponseModel(com.mordent.ua.mediaservice.model.domain.Song song, User user, Album album);

    ListenResponse toListenResponse(com.mordent.ua.mediaservice.model.domain.Song song);

    SongWithMetadataResponse toResponseModel(com.mordent.ua.mediaservice.model.domain.SongWithMetadata songWithMetadata);

    SongStatisticResponse toResponseModel(SongStatistic songStatistic);

    @Named("toSongUser")
    static SongWithMetadata.SongUser toSongUser(final User user) {
        return new SongWithMetadata.SongUser(user.id(), user.name(), user.surname(), user.username());
    }

    @Named("toSongAlbum")
    static SongWithMetadata.SongAlbum toSongAlbum(final Album album) {
        return new SongWithMetadata.SongAlbum(album.id(), album.name());
    }
}
