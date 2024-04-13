package com.mordent.ua.mediaservice.controller;

import com.mordent.ua.mediaservice.interactor.AlbumInteractor;
import com.mordent.ua.mediaservice.model.body.request.AlbumRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumInteractor albumInteractor;

    @GetMapping
    public Flux<AlbumResponse> findAll(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(value = "owned", required = false) boolean owned,
            @RequestParam(value = "name", required = false) String name
    ) {
        if (owned) {
            return albumInteractor.findAllByUserId(userSecurity.id());
        } else {
            return albumInteractor.findAll(userSecurity.id(), name);
        }
    }

    @GetMapping("{id}")
    public Mono<AlbumResponse> findById(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @PathVariable(name = "id") Long albumId
    ) {
        return albumInteractor.findById(userSecurity.id(), albumId);
    }

    @GetMapping("statistic")
    public Mono<AlbumStatisticResponse> getStatisticById(
            @RequestParam(name = "id") Long albumId
    ) {
        return albumInteractor.getStatisticById(albumId);
    }

    @GetMapping("songs")
    public Flux<SongWithMetadataResponse> findAllAlbumSongs(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long albumId
    ) {
        return albumInteractor.findAllSongs(userSecurity.id(), albumId);
    }

    @GetMapping("liked")
    public Flux<AlbumResponse> findAllLikedSongs(
            @AuthenticationPrincipal UserSecurity userSecurity
    ) {
        return albumInteractor.findAllLiked(userSecurity.id());
    }

    @PostMapping
    public Mono<AlbumResponse> save(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestPart(value = "request") AlbumRequest request,
            @RequestPart(value = "image", required = false) FilePart image
    ) {
        request = request.toBuilder()
                .userId(userSecurity.id())
                .imageFilename(image == null ? null : image.filename())
                .build();
        return albumInteractor.save(request, image);
    }

    @PostMapping("toggle-like")
    public Mono<LikeResponse> toggleLike(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long albumId
    ) {
        return albumInteractor.toggleLike(userSecurity.id(), albumId);
    }

    @PostMapping("toggle-song")
    public Mono<AlbumSongResponse> toggleSongs(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "albumId") Long albumId,
            @RequestParam(name = "songId") Long songId
    ) {
        return albumInteractor.toggleSong(userSecurity.id(), albumId, songId);
    }

    @PutMapping
    public Mono<AlbumResponse> update(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestPart(value = "request") AlbumRequest request,
            @RequestPart(value = "image", required = false) FilePart image
    ) {
        request = request.toBuilder()
                .imageFilename(image == null ? null : image.filename())
                .build();
        return albumInteractor.update(userSecurity, request);
    }

    @DeleteMapping
    public Mono<Void> delete(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long albumId
    ) {
        return albumInteractor.delete(userSecurity, albumId);
    }
}
