package com.mordent.ua.mediaservice.service.impl;

import com.mordent.ua.mediaservice.model.domain.Album;
import com.mordent.ua.mediaservice.model.domain.Playlist;
import com.mordent.ua.mediaservice.model.domain.Song;
import com.mordent.ua.mediaservice.model.domain.User;
import com.mordent.ua.mediaservice.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String AVATAR_FILEPATH = "avatars";
    private static final String SONG_FILEPATH = "songs";
    private static final String ALBUM_FILEPATH = "albums";
    private static final String PLAYLIST_FILEPATH = "playlists";

    private String imageBaseFilepath;
    private String songBaseFilepath;
    private final Environment env;

    @PostConstruct
    void initialize() {
        try {
            if (Arrays.stream(env.getActiveProfiles()).anyMatch(profile -> profile.equals("localDocker") || profile.equals("prod"))) {
                imageBaseFilepath = "/static/files/images";
                songBaseFilepath = "/static/files/songs";
            } else {
                final String baseFilepath = ResourceUtils.getFile("classpath:").getPath();
                imageBaseFilepath = baseFilepath + "/static/files/images";
                songBaseFilepath = baseFilepath + "/static/files/songs";
            }
            File directory = new File(songBaseFilepath);
            if (!directory.exists()) {
                Files.createDirectories(directory.toPath());
            }
            final String[] filepathes = new String[]{AVATAR_FILEPATH, SONG_FILEPATH, ALBUM_FILEPATH, PLAYLIST_FILEPATH};
            for (String filepath : filepathes) {
                directory = new File(String.format("%s/%s", imageBaseFilepath, filepath));
                if (!directory.exists()) {
                    Files.createDirectories(directory.toPath());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Mono<String> updateUserAvatar(final User user, final FilePart avatarFilePart) {
        String avatarFilepath;
        File avatarFile;
        if (user.avatar() != null && !user.avatar().startsWith("http")) {
            avatarFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, AVATAR_FILEPATH, user.id(), user.avatar());
            avatarFile = new File(avatarFilepath);
            if (avatarFile.exists()) {
                avatarFile.delete();
            }
        }
        if (avatarFilePart == null) {
            return Mono.empty();
        }

        avatarFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, AVATAR_FILEPATH, user.id(), avatarFilePart.filename());
        avatarFile = new File(avatarFilepath);
        return avatarFilePart.transferTo(avatarFile)
                .thenReturn(avatarFile.getAbsolutePath());
    }

    @Override
    public Mono<String> saveSong(final Song song, final FilePart imageFilePart, final FilePart songFilePart) {
        String imageFilepath;
        File imageFile = null;
        if (imageFilePart != null) {
            imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, SONG_FILEPATH, song.id(), imageFilePart.filename());
            imageFile = new File(imageFilepath);
        }
        String songFilepath = String.format("%s/%d%s", songBaseFilepath, song.id(), songFilePart.filename());
        File songFile = new File(songFilepath);
        return songFilePart.transferTo(songFile)
                .then(imageFilePart == null ? Mono.empty() : imageFilePart.transferTo(imageFile))
                .thenReturn(songFile.getAbsolutePath());
    }

    @Override
    public Mono<String> updateSong(final Song song, final FilePart imageFilePart) {
        String imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, SONG_FILEPATH, song.id(), imageFilePart.filename());
        File imageFile = new File(imageFilepath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, SONG_FILEPATH, song.id(), imageFilePart.filename());
        imageFile = new File(imageFilepath);
        return imageFilePart.transferTo(imageFile)
                .thenReturn(imageFile.getAbsolutePath());
    }

    @Override
    public Mono<String> updateAlbum(Album album, FilePart imageFilePart) {
        String imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, ALBUM_FILEPATH, album.id(), album.imageFilename());
        File imageFile = new File(imageFilepath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, ALBUM_FILEPATH, album.id(), imageFilePart.filename());
        imageFile = new File(imageFilepath);
        return imageFilePart.transferTo(imageFile)
                .thenReturn(imageFile.getAbsolutePath());
    }

    @Override
    public Mono<String> updatePlaylist(final Playlist playlist, final FilePart imageFilePart) {
        String imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, PLAYLIST_FILEPATH, playlist.id(), playlist.imageFilename());
        File imageFile = new File(imageFilepath);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        imageFilepath = String.format("%s/%s/%d%s", imageBaseFilepath, PLAYLIST_FILEPATH, playlist.id(), imageFilePart.filename());
        imageFile = new File(imageFilepath);
        return imageFilePart.transferTo(imageFile)
                .thenReturn(imageFile.getAbsolutePath());
    }
}
