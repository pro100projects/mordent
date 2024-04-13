import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { ERROR_INITIAL_STATE, SLICE_INITIAL_STATE } from '../constants';
import {
  deleteSongRequest,
  getLikedSongsRequest,
  getSongRequest,
  getSongsRequest,
  getSongStatisticRequest,
  listenSongRequest,
  saveSongRequest,
  toggleLikeSongRequest,
  updateSongRequest
} from '../../requests/song.requests';
import { toast } from 'react-toastify';
import { selectPlayerSong, setPlayerSong, setPlayerSongParameters } from '../player';

export const SONG_REDUCER_KEY = 'song';

const SONG_INITIAL_STATE = {
  songs: [],
  userSongs: [],
  likedSongs: [],
  searchSongs: []
};

export const getSongsThunk = createAsyncThunk('get songs', async (request, { rejectWithValue }) => {
  try {
    const response = await getSongsRequest();
    return response.data;
  } catch (error) {
    if (error.error?.code) {
      return rejectWithValue(error);
    }
    return rejectWithValue(error.response?.data?.error);
  }
});

export const searchSongsThunk = createAsyncThunk(
  'search songs',
  async (name, { rejectWithValue }) => {
    try {
      const response = await getSongsRequest(name);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getSongThunk = createAsyncThunk(
  'get song',
  async ({ songId, play = true }, { dispatch, rejectWithValue }) => {
    try {
      const response = await getSongRequest(songId);
      dispatch(setPlayerSong({ song: response.data, play }));
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getSongStatisticThunk = createAsyncThunk(
  'get song statistic',
  async (songId, { rejectWithValue }) => {
    try {
      const response = await getSongStatisticRequest(songId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getLikedSongsThunk = createAsyncThunk(
  'get liked songs',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getLikedSongsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const saveSongThunk = createAsyncThunk(
  'save song',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await saveSongRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const listenSongThunk = createAsyncThunk(
  'listen song',
  async (songId, { rejectWithValue }) => {
    try {
      const response = await listenSongRequest(songId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const toggleLikeSongThunk = createAsyncThunk(
  'toggle like song',
  async (songId, { dispatch, rejectWithValue, getState }) => {
    try {
      const response = await toggleLikeSongRequest(songId);
      const song = selectPlayerSong(getState());
      if (response.data && song.id === songId) {
        dispatch(setPlayerSongParameters({ liked: response.data.liked }));
      }
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const updateSongThunk = createAsyncThunk(
  'update song',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await updateSongRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const deleteSongThunk = createAsyncThunk(
  'delete song',
  async (songId, { rejectWithValue }) => {
    try {
      await deleteSongRequest(songId);
      return { songId };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const songSlice = createSlice({
  name: SONG_REDUCER_KEY,
  initialState: SLICE_INITIAL_STATE(SONG_INITIAL_STATE),
  reducers: {
    mixSongs: (state, action) => {
      state.data.songs = linkSongsTogether(state.data.songs, action.payload);
      state.data.userSongs = linkSongsTogether(state.data.userSongs, action.payload);
      state.data.likedSongs = linkSongsTogether(state.data.likedSongs, action.payload);
    }
  },
  extraReducers: (builder) => {
    builder.addCase(getSongsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.songs = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getSongsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.songs = action.payload
        .sort((a, b) => b.playback - a.playback)
        .map((currentSong) => {
          const currentIndex = action.payload.findIndex((song) => song.id === currentSong.id);
          const prevSong = action.payload[currentIndex - 1];
          const nextSong = action.payload[currentIndex + 1];
          return {
            ...currentSong,
            prev: prevSong ? prevSong.id : action.payload[action.payload.length - 1].id,
            next: nextSong ? nextSong.id : action.payload[0].id
          };
        });
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getSongsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.songs = [];
      state.error = action.payload;
    });

    builder.addCase(searchSongsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.searchSongs = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(searchSongsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.searchSongs = action.payload
        .sort((a, b) => b.playback - a.playback)
        .map((currentSong) => {
          const currentIndex = action.payload.findIndex((song) => song.id === currentSong.id);
          const prevSong = action.payload[currentIndex - 1];
          const nextSong = action.payload[currentIndex + 1];
          return {
            ...currentSong,
            prev: prevSong ? prevSong.id : action.payload[action.payload.length - 1].id,
            next: nextSong ? nextSong.id : action.payload[0].id
          };
        });
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(searchSongsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.searchSongs = [];
      state.error = action.payload;
    });

    builder.addCase(getSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      if (!state.data.songs.find((song) => song.id === action.payload.id)) {
        state.data.songs = [
          ...state.data.songs,
          {
            ...action.payload,
            prev: state.data.songs[state.data.songs.length - 1].id,
            next: state.data.songs[0].id
          }
        ];
      }
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getSongStatisticThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getSongStatisticThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.songs = state.data.songs.map((song) => {
        if (song.id === action.payload.id) {
          return { ...song, likes: action.payload.likes };
        }
        return song;
      });
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getSongStatisticThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getLikedSongsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.likedSongs = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getLikedSongsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.likedSongs = action.payload
        .sort((a, b) => b.playback - a.playback)
        .map((currentSong) => {
          const currentIndex = action.payload.findIndex((song) => song.id === currentSong.id);
          const prevSong = action.payload[currentIndex - 1];
          const nextSong = action.payload[currentIndex + 1];
          return {
            ...currentSong,
            prev: prevSong ? prevSong.id : action.payload[action.payload.length - 1].id,
            next: nextSong ? nextSong.id : action.payload[0].id
          };
        });
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getLikedSongsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.likedSongs = [];
      state.error = action.payload;
    });

    builder.addCase(saveSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(saveSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      if (!state.data.songs || state.data.songs.length === 0) {
        state.data.songs = [
          {
            ...action.payload,
            prev: action.payload.id,
            next: action.payload.id
          }
        ];
      } else if (state.data.songs.length === 1) {
        state.data.songs = [
          {
            ...state.data.songs[0],
            prev: action.payload.id,
            next: action.payload.id
          },
          {
            ...action.payload,
            prev: state.data.songs[0].id,
            next: state.data.songs[0].id
          }
        ];
      } else {
        state.data.songs = [
          {
            ...state.data.songs[0],
            prev: action.payload.id
          },
          ...state.data.songs.filter(
            (song) =>
              song.id !== state.data.songs[0].id &&
              song.id !== state.data.songs[state.data.songs.length - 1].id
          ),
          {
            ...state.data.songs[state.data.songs.length - 1],
            next: action.payload.id
          },
          {
            ...action.payload,
            prev: state.data.songs[state.data.songs.length - 1].id,
            next: state.data.songs[0].id
          }
        ];
      }
      state.error = ERROR_INITIAL_STATE;
      toast.success('Song is successful added');
    });
    builder.addCase(saveSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      toast.warn('We have some troubles with saving song');
    });

    builder.addCase(listenSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(listenSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.songs = state.data.songs.map((song) => {
        if (song.id === action.payload.id) {
          return { ...song, playback: action.payload.playback };
        }
        return song;
      });
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(listenSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(toggleLikeSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleLikeSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.songs = state.data.songs.map((song) => {
        if (song.id === action.payload.id) {
          return { ...song, liked: action.payload.liked };
        }
        return song;
      });
      if (action.payload.liked) {
        const song = state.data.songs.find((s) => s.id === action.payload.id);
        state.data.likedSongs = [...state.data.likedSongs, song];
      } else {
        state.data.likedSongs = state.data.likedSongs.filter(
          (song) => song.id !== action.payload.id
        );
      }
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleLikeSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(updateSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.songs = state.data.songs.map((song) => {
        if (song.id === action.payload.id) {
          return action.payload;
        }
        return song;
      });
      state.data.userSongs = state.data.userSongs.map((song) => {
        if (song.id === action.payload.id) {
          return action.payload;
        }
        return song;
      });
      state.data.likedSongs = state.data.likedSongs.map((song) => {
        if (song.id === action.payload.id) {
          return action.payload;
        }
        return song;
      });
      state.error = ERROR_INITIAL_STATE;
      toast.success('Song is successful updated');
    });
    builder.addCase(updateSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      if (action.payload.code === 'MEDIA100003') {
        toast.warn(action.payload.description);
      } else {
        toast.warn('We have some troubles with updating song');
      }
    });

    builder.addCase(deleteSongThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deleteSongThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.songs = state.data.songs.filter((song) => song.id !== action.payload.songId);
      state.data.userSongs = state.data.userSongs.filter(
        (song) => song.id !== action.payload.songId
      );
      state.data.likedSongs = state.data.likedSongs.filter(
        (song) => song.id !== action.payload.songId
      );
      state.error = ERROR_INITIAL_STATE;
      toast.success('Song is successful deleted');
    });
    builder.addCase(deleteSongThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      if (action.payload.code === 'MEDIA100003') {
        toast.warn(action.payload.description);
      } else {
        toast.warn('We have some troubles with deleting song');
      }
    });
  }
});

const linkSongsTogether = (songs, isRandom) => {
  const mixedSongs = songs
    .sort((a, b) => {
      if (isRandom) {
        return Math.random() * 2 - 1;
      } else {
        return b.playback - a.playback;
      }
    })
    .map((currentSong) => {
      const currentIndex = songs.findIndex((song) => song.id === currentSong.id);
      const prevSong = songs[currentIndex - 1];
      const nextSong = songs[currentIndex + 1];
      return {
        ...currentSong,
        prev: prevSong ? prevSong.id : songs[songs.length - 1].id,
        next: nextSong ? nextSong.id : songs[0].id
      };
    });
  return songs
    .sort((a, b) => b.playback - a.playback)
    .map((currentSong) => {
      return mixedSongs.find((song) => song.id === currentSong.id);
    });
};

export const { reducer: songReducer } = songSlice;

export const { mixSongs } = songSlice.actions;
