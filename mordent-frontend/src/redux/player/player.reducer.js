import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { ERROR_INITIAL_STATE, SLICE_INITIAL_STATE } from '../constants';
import { getSongRequest } from '../../requests/song.requests';
import { getAlbumSongsRequest } from '../../requests/album.requests';
import { getPlaylistSongsRequest } from '../../requests/playlist.requests';

export const PLAYER_REDUCER_KEY = 'player';

const PLAYER_INITIAL_STATE = {
  song: {},
  album: {},
  playlist: {},
  queue: [],
  settings: {
    play: false,
    time: 0,
    volume: 100,
    mix: false,
    repeat: null,
    repeatOneSong: false
  }
};

export const handlePlayAlbumThunk = createAsyncThunk(
  'handle play album',
  async ({ albums, albumId, songs }, { rejectWithValue }) => {
    try {
      if (songs && songs.length > 0) {
        return {
          albumId: albumId,
          ids: songs.map((song) => song.id),
          song: songs[0]
        };
      }

      if (albums) {
        const album = albums.find((a) => a.id === albumId);
        if (album && album.songIds && album.songIds.length > 0) {
          const songId = album.songIds[0];
          let song;
          if (album.songs && album.songs.length > 0) {
            song = album.songs.find((s) => s.id === songId);
          }

          if (!song) {
            const response = await getSongRequest(songId);
            song = response.data;
            //dispatch(addSongToAlbum(albumId, song));
          }

          return { albumId: album.id, ids: album.songIds, song };
        }
      }

      const response = await getAlbumSongsRequest(albumId);
      //dispatch(addSongsToAlbum(albumId, song));
      return {
        albumId: albumId,
        ids: Array.from(response.data).map((song) => song.id),
        song: response.data.length > 0 ? response.data[0] : {}
      };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const handlePlayPlaylistThunk = createAsyncThunk(
  'handle play playlist',
  async ({ playlists, playlistId, songs }, { rejectWithValue }) => {
    try {
      if (songs && songs.length > 0) {
        return {
          playlistId: playlistId,
          ids: songs.map((song) => song.id),
          song: songs[0]
        };
      }

      if (playlists) {
        const playlist = playlists.find((p) => p.id === playlistId);
        if (playlist && playlist.songIds && playlist.songIds.length > 0) {
          const songId = playlist.songIds[0];
          let song;
          if (playlist.songs && playlist.songs.length > 0) {
            song = playlist.songs.find((s) => s.id === songId);
          }

          if (!song) {
            const response = await getSongRequest(songId);
            song = response.data;
            //dispatch(addSongToPlaylist(playlistId, song));
          }

          return { playlistId: playlist.id, ids: playlist.songIds, song };
        }
      }

      const response = await getPlaylistSongsRequest(playlistId);
      //dispatch(addSongsToPlaylist(playlistId, song));
      return {
        playlistId: playlistId,
        ids: Array.from(response.data).map((song) => song.id),
        song: response.data.length > 0 ? response.data[0] : {}
      };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const handleNextSongThunk = createAsyncThunk(
  'handle next song',
  async ({ songId, play = true }, { rejectWithValue }) => {
    try {
      const response = await getSongRequest(songId);
      return { song: response.data, play };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const playerSlice = createSlice({
  name: PLAYER_REDUCER_KEY,
  initialState: SLICE_INITIAL_STATE(PLAYER_INITIAL_STATE),
  reducers: {
    setPlayerSong: (state, action) => {
      state.data = {
        ...state.data,
        song: action.payload.song,
        settings: { ...state.data.settings, play: action.payload.play }
      };
      localStorage.setItem('song', JSON.stringify(action.payload));
    },
    setPlayerPlaylist: (state, action) => {
      state.data = {
        ...state.data,
        song: action.payload.songs[0],
        playlist: action.payload,
        settings: { ...state.data.settings, play: true }
      };
      localStorage.setItem('playlist', JSON.stringify(action.payload));
    },
    setQueue: (state, action) => {
      state.data.queue = action.payload;
      localStorage.setItem('queue', JSON.stringify(action.payload));
    },
    setPlayerSongParameters: (state, action) => {
      state.data.song = {
        ...state.data.song,
        ...action.payload
      };
    },
    setPlayerSetting: (state, action) => {
      state.data.settings = { ...state.data.settings, ...action.payload };
    }
  },
  extraReducers: (builder) => {
    builder.addCase(handlePlayAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(handlePlayAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = {
        song: action.payload.song,
        album: { id: action.payload.albumId },
        playlist: {},
        queue: action.payload.ids,
        settings: { ...state.data.settings, play: true }
      };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(handlePlayAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(handlePlayPlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(handlePlayPlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = {
        song: action.payload.song,
        album: {},
        playlist: { id: action.payload.playlistId },
        queue: action.payload.ids,
        settings: { ...state.data.settings, play: true }
      };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(handlePlayPlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(handleNextSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(handleNextSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = {
        ...state.data,
        song: action.payload.song,
        settings: { ...state.data.settings, play: action.payload.play }
      };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(handleNextSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });
  }
});

export const {
  setPlayerSong,
  setPlayerPlaylist,
  setQueue,
  setPlayerSongParameters,
  setPlayerSetting
} = playerSlice.actions;

export const { reducer: playerReducer } = playerSlice;
