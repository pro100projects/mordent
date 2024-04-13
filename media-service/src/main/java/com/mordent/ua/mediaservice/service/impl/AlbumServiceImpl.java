package com.mordent.ua.mediaservice.service.impl;

import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import com.mordent.ua.mediaservice.repository.AlbumRepository;
import com.mordent.ua.mediaservice.repository.SongRepository;
import com.mordent.ua.mediaservice.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    @Override
    public Flux<Album> findAll(final String name) {
        if (name == null || name.trim().isEmpty()) {
            return albumRepository.findAll();
        } else {
            return albumRepository.findAllByNameIsContainingIgnoreCase(name.trim());
        }
    }

    @Override
    public Mono<Album> findById(final Long albumId) {
        return albumRepository.findById(albumId)
                .switchIfEmpty(Mono.error(new MediaException(ErrorCode.ALBUM_NOT_FOUND, "Album is not exist")));
    }

    @Override
    public Flux<Album> findAllByUserId(final Long userId) {
        return albumRepository.findAllByUserId(userId);
    }

    @Override
    public Flux<Like> findAllLiked(final Long userId) {
        return albumRepository.findAllLikeAlbumsByUserId(userId);
    }

    @Override
    public Mono<Like> findLikeById(final Long userId, final Long albumId) {
        return albumRepository.findAlbumLike(userId, albumId);
    }

    @Override
    public Mono<Album> save(final Album album) {
        return albumRepository.save(album);
    }

    @Override
    public Mono<Boolean> toggleLike(final Long userId, final Long albumId) {
        return albumRepository.findAlbumLike(userId, albumId)
                .flatMap(songLike -> songLike.liked() ?
                        albumRepository.deleteAlbumLike(userId, albumId).map(flag -> !flag)
                        :
                        albumRepository.saveAlbumLike(userId, albumId)
                );
    }


    /*@Override
    public Mono<Boolean> toggleSong(final Long userId, final Long albumId, final Long songId) {
        return findById(albumId)
                .flatMap(album -> {
                    if (!album.userId().equals(userId)) {
                        return Mono.error(new MediaException(ErrorCode.NOT_ALLOWED, "Album can only be changed by its owner"));
                    }
                    return albumRepository.findAlbumSong(album.id(), songId)
                                    .flatMap(albumSong -> albumSong.songId() == null ?
                                            albumRepository.saveAlbumSong(albumSong.albumId(), songId)
                                            :
                                            albumRepository.deleteAlbumSong(albumSong.albumId(), albumSong.songId()).map(flag -> !flag)
                                    );
                        }
                );
    }*/
    @Override
    public Mono<Album> update(final Album oldAlbum, final Album newAlbum) {
        return albumRepository.save(oldAlbum.overwritingVariables(newAlbum));
    }

    @Override
    public Mono<Void> delete(final Album album) {
        return songRepository.findAllByAlbumId(album.id())
                .map(song -> song.toBuilder().albumId(null).build())
                .flatMap(songRepository::save)
                .collectList()
                .flatMap(songs -> albumRepository.deleteAlbumLikes(album.id())
                        .then(albumRepository.delete(album))
                )
                .onErrorResume(error -> Mono.error(new MediaException(ErrorCode.UNEXPECTED, error.getMessage())));

    }

    @Override
    public Mono<Long> getCountAlbumLikes(final Long albumId) {
        return albumRepository.getCountAlbumLikes(albumId);
    }

    @Override
    public Mono<Long> getTotalPlaybackByAlbumId(final Long albumId) {
        return songRepository.getTotalPlaybackByAlbumId(albumId).switchIfEmpty(Mono.just(0L));
    }

    @Override
    public Mono<Long> getCountSongsLikesInAlbum(final Long albumId) {
        return songRepository.getCountSongsLikesInAlbum(albumId);
    }
}
