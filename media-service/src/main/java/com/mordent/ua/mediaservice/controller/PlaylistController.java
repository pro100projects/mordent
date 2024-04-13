package com.mordent.ua.mediaservice.controller;

import com.mordent.ua.mediaservice.interactor.PlaylistInteractor;
import com.mordent.ua.mediaservice.model.body.request.PlaylistRequest;
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
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistInteractor playlistInteractor;

    @GetMapping
    public Flux<PlaylistResponse> findAll(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(value = "owned", required = false) boolean owned,
            @RequestParam(value = "name", required = false) String name
    ) {
        if(owned) {
            return playlistInteractor.findAllByUserId(userSecurity.id());
        } else {
            return playlistInteractor.findAll(userSecurity.id(), name);
        }
    }

    @GetMapping("{id}")
    public Mono<PlaylistResponse> findById(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @PathVariable(name = "id") Long playlistId
    ) {
        return playlistInteractor.findById(userSecurity, playlistId);
    }

    @GetMapping("statistic")
    public Mono<PlaylistStatisticResponse> getStatisticById(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long playlistId
    ) {
        return playlistInteractor.getStatisticById(userSecurity, playlistId);
    }

    @GetMapping("songs")
    public Flux<SongWithMetadataResponse> findAllPlaylistSongs(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long playlistId
    ) {
        return playlistInteractor.findAllSongs(userSecurity, playlistId);
    }

    @GetMapping("liked")
    public Flux<PlaylistResponse> findAllLikedSongs(
            @AuthenticationPrincipal UserSecurity userSecurity
    ) {
        return playlistInteractor.findAllLiked(userSecurity.id());
    }

    @PostMapping
    public Mono<PlaylistResponse> save(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestPart(value = "request") PlaylistRequest request,
            @RequestPart(value = "image", required = false) FilePart image
    ) {
        request = request.toBuilder()
                .userId(userSecurity.id())
                .imageFilename(image == null ? null : image.filename())
                .build();
        return playlistInteractor.save(request, image);
    }

    @PostMapping("toggle-like")
    public Mono<LikeResponse> toggleLike(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long playlistId
    ) {
        return playlistInteractor.toggleLike(userSecurity.id(), playlistId);
    }

    @PostMapping("toggle-song")
    public Mono<PlaylistSongResponse> toggleSongs(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "playlistId") Long playlistId,
            @RequestParam(name = "songId") Long songId
    ) {
        return playlistInteractor.toggleSong(userSecurity, playlistId, songId);
    }

    @PutMapping
    public Mono<PlaylistResponse> update(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestPart(value = "request") PlaylistRequest request,
            @RequestPart(value = "image", required = false) FilePart image
    ) {
        request = request.toBuilder()
                .imageFilename(image == null ? null : image.filename())
                .build();
        return playlistInteractor.update(userSecurity, request);
    }

    @DeleteMapping
    public Mono<Void> delete(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long playlistId
    ) {
        return playlistInteractor.delete(userSecurity, playlistId);
    }
}
