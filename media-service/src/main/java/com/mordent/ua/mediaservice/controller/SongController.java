package com.mordent.ua.mediaservice.controller;

import com.mordent.ua.mediaservice.interactor.SongInteractor;
import com.mordent.ua.mediaservice.model.body.request.SongRequest;
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
@RequestMapping("/api/songs")
public class SongController {

    private final SongInteractor songInteractor;

    @GetMapping
    public Flux<SongWithMetadataResponse> findAll(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(value = "name", required = false) String name
    ) {
        return songInteractor.findAll(userSecurity.id(), name);
    }

    @GetMapping("{id}")
    public Mono<SongWithMetadataResponse> findById(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @PathVariable(name = "id") Long songId
    ) {
        return songInteractor.findById(userSecurity.id(), songId);
    }

    @GetMapping("statistic")
    public Mono<SongStatisticResponse> getStatisticById(
            @RequestParam(name = "id") Long songId
    ) {
        return songInteractor.getStatisticById(songId);
    }

    @GetMapping("liked")
    public Flux<SongWithMetadataResponse> findAllLikedSongs(
            @AuthenticationPrincipal UserSecurity userSecurity
    ) {
        return songInteractor.findAllLiked(userSecurity.id());
    }


    @PostMapping
    public Mono<SongResponse> save(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestPart(value = "request") SongRequest request,
            @RequestPart(value = "image", required = false) FilePart image,
            @RequestPart(value = "song") FilePart song
    ) {
        request = request.toBuilder()
                .userId(userSecurity.id())
                .imageFilename(image == null ? null : image.filename())
                .songFilename(song.filename())
                .build();
        return songInteractor.save(userSecurity, request, image, song);
    }

    @PostMapping("{id}")
    public Mono<ListenResponse> listen(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @PathVariable(name = "id") Long songId
    ) {
        return songInteractor.listen(userSecurity, songId);
    }

    @PostMapping("toggle-like")
    public Mono<LikeResponse> toggleLike(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long songId
    ) {
        return songInteractor.toggleLike(userSecurity.id(), songId);
    }

    @PutMapping
    public Mono<SongWithMetadataResponse> update(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @Valid @RequestPart(value = "request") SongRequest request,
            @RequestPart(value = "image", required = false) FilePart image
    ) {
        request = request.toBuilder()
                .imageFilename(image == null ? null : image.filename())
                .build();
        return songInteractor.update(userSecurity, request);
    }

    @DeleteMapping
    public Mono<Void> delete(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "id") Long songId
    ) {
        return songInteractor.delete(userSecurity, songId);
    }
}
