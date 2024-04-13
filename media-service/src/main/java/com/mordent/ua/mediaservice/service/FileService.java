package com.mordent.ua.mediaservice.service;

import com.mordent.ua.mediaservice.model.domain.Album;
import com.mordent.ua.mediaservice.model.domain.Playlist;
import com.mordent.ua.mediaservice.model.domain.Song;
import com.mordent.ua.mediaservice.model.domain.User;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileService {

    Mono<String> updateUserAvatar(User user, FilePart avatarFilePart);

    Mono<String> saveSong(Song song, FilePart imageFilePart, FilePart songFilePart);

    Mono<String> updateSong(Song song, FilePart imageFilePart);

    Mono<String> updateAlbum(Album album, FilePart imageFilePart);

    Mono<String> updatePlaylist(Playlist playlist, FilePart imageFilePart);
}
