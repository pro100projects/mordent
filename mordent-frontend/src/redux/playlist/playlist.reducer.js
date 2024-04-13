import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { ERROR_INITIAL_STATE, SLICE_INITIAL_STATE } from '../constants';
import {
  deletePlaylistRequest,
  getLikedPlaylistsRequest,
  getPlaylistRequest,
  getPlaylistSongsRequest,
  getPlaylistsRequest,
  getPlaylistStatisticRequest,
  getUserPlaylistsRequest,
  savePlaylistRequest,
  toggleLikePlaylistRequest,
  toggleSongPlaylistRequest,
  updatePlaylistRequest
} from '../../requests/playlist.requests';
import { toast } from 'react-toastify';

export const PLAYLIST_REDUCER_KEY = 'playlist';

const PLAYLIST_INITIAL_STATE = {
  playlists: [],
  userPlaylists: [],
  likedPlaylists: [],
  searchPlaylists: [],
  selectedPlaylist: null
};

export const getPlaylistsThunk = createAsyncThunk(
  'get playlists',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getPlaylistsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const searchPlaylistsThunk = createAsyncThunk(
  'search playlists',
  async (name, { rejectWithValue }) => {
    try {
      const response = await getPlaylistsRequest(name);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getPlaylistThunk = createAsyncThunk(
  'get playlist',
  async (playlistId, { dispatch, rejectWithValue }) => {
    try {
      const response = await getPlaylistRequest(playlistId);
      dispatch(getPlaylistSongsThunk(playlistId));
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getPlaylistStatisticThunk = createAsyncThunk(
  'get playlist statistic',
  async (playlistId, { rejectWithValue }) => {
    try {
      const response = await getPlaylistStatisticRequest(playlistId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getPlaylistSongsThunk = createAsyncThunk(
  'get playlist songs',
  async (playlistId, { rejectWithValue }) => {
    try {
      const response = await getPlaylistSongsRequest(playlistId);
      return { id: playlistId, songs: response.data };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getUserPlaylistsThunk = createAsyncThunk(
  'get user playlists',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getUserPlaylistsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getLikedPlaylistsThunk = createAsyncThunk(
  'get liked playlists',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getLikedPlaylistsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const savePlaylistThunk = createAsyncThunk(
  'save playlist',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await savePlaylistRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const toggleLikePlaylistThunk = createAsyncThunk(
  'toggle like playlist',
  async (playlistId, { rejectWithValue }) => {
    try {
      const response = await toggleLikePlaylistRequest(playlistId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const toggleSongPlaylistThunk = createAsyncThunk(
  'toggle song playlist',
  async ({ playlistId, songId }, { rejectWithValue }) => {
    try {
      const response = await toggleSongPlaylistRequest(playlistId, songId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const updatePlaylistThunk = createAsyncThunk(
  'update playlist',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await updatePlaylistRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const deletePlaylistThunk = createAsyncThunk(
  'delete playlist',
  async (playlistId, { rejectWithValue }) => {
    try {
      await deletePlaylistRequest(playlistId);
      return { playlistId };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const playlistSlice = createSlice({
  name: PLAYLIST_REDUCER_KEY,
  initialState: SLICE_INITIAL_STATE(PLAYLIST_INITIAL_STATE),
  reducers: {
    setSelectedPlaylist: (state, action) => {
      state.data.selectedPlaylist = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(getPlaylistsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.playlists = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.playlists = [];
      state.error = action.payload;
    });

    builder.addCase(searchPlaylistsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.searchPlaylists = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(searchPlaylistsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.searchPlaylists = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(searchPlaylistsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.searchPlaylists = [];
      state.error = action.payload;
    });

    builder.addCase(getPlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      if (!state.data.playlists.find((playlist) => playlist.id === action.payload.id)) {
        state.data.playlists = [...state.data.playlists, action.payload];
      }
      if (
        action.payload.liked &&
        !state.data.playlists.find((playlist) => playlist.id === action.payload.id)
      ) {
        state.data.likedPlaylists = [...state.data.likedPlaylists, action.payload];
      }
      //todo: userPlaylists
      state.data.selectedPlaylist = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getPlaylistStatisticThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistStatisticThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = state.data.playlists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, likes: action.payload.likes };
        }
        return playlist;
      });
      state.data.userPlaylists = state.data.userPlaylists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, likes: action.payload.likes };
        }
        return playlist;
      });
      state.data.likedPlaylists = state.data.likedPlaylists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, likes: action.payload.likes };
        }
        return playlist;
      });
      if (state.data.selectedPlaylist?.id === action.payload.id) {
        state.data.selectedPlaylist = {
          ...state.data.selectedPlaylist,
          likes: action.payload.likes
        };
      }
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistStatisticThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getPlaylistSongsThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistSongsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = state.data.playlists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, songs: action.payload.songs };
        }
        return playlist;
      });
      state.data.userPlaylists = state.data.userPlaylists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, songs: action.payload.songs };
        }
        return playlist;
      });
      state.data.likedPlaylists = state.data.likedPlaylists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, songs: action.payload.songs };
        }
        return playlist;
      });
      state.data.selectedPlaylist = {
        ...state.data.selectedPlaylist,
        songs: action.payload.songs
      };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getPlaylistSongsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getUserPlaylistsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.userPlaylists = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getUserPlaylistsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.userPlaylists = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getUserPlaylistsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.userPlaylists = [];
      state.error = action.payload;
    });

    builder.addCase(getLikedPlaylistsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.likedPlaylists = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getLikedPlaylistsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.likedPlaylists = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getLikedPlaylistsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.likedPlaylists = [];
      state.error = action.payload;
    });

    builder.addCase(savePlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(savePlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = [...state.data.playlists, action.payload];
      state.data.userPlaylists = [...state.data.userPlaylists, action.payload];
      state.error = ERROR_INITIAL_STATE;
      toast.success('Playlist is successful added');
    });
    builder.addCase(savePlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      toast.warn('We have some troubles with saving playlist');
    });

    builder.addCase(toggleLikePlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleLikePlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = state.data.playlists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...playlist, liked: action.payload.liked };
        }
        return playlist;
      });
      if (action.payload.liked) {
        const playlist = state.data.playlists.find((p) => p.id === action.payload.id);
        state.data.likedPlaylists = [...state.data.likedPlaylists, playlist];
      } else {
        state.data.likedPlaylists = state.data.likedPlaylists.filter(
          (playlist) => playlist.id !== action.payload.id
        );
      }
      if (state.data.selectedPlaylist?.id === action.payload.id) {
        state.data.selectedPlaylist = {
          ...state.data.selectedPlaylist,
          liked: action.payload.liked
        };
      }
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleLikePlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(toggleSongPlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleSongPlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      if (state.data.selectedPlaylist?.id === action.payload.playlistId) {
        if (!action.payload.saved) {
          state.data.selectedPlaylist = {
            ...state.data.selectedPlaylist,
            songs: action.payload.songs.filter((song) => song.id !== action.payload.songId)
          };
        }
      }
      //todo: add other
      state.error = ERROR_INITIAL_STATE;
      toast.success('Song is successful added');
    });
    builder.addCase(toggleSongPlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      toast.warn('We have some troubles with adding song to playlist');
    });

    builder.addCase(updatePlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updatePlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = state.data.playlists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...action.payload, songs: playlist.songs };
        }
        return playlist;
      });
      state.data.userPlaylists = state.data.userPlaylists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...action.payload, songs: playlist.songs };
        }
        return playlist;
      });
      state.data.likedPlaylists = state.data.likedPlaylists.map((playlist) => {
        if (playlist.id === action.payload.id) {
          return { ...action.payload, songs: playlist.songs };
        }
        return playlist;
      });
      if (state.data.selectedPlaylist?.id === action.payload.id) {
        state.data.selectedPlaylist = {
          ...action.payload,
          songs: state.data.selectedPlaylist.songs
        };
      }
      state.error = ERROR_INITIAL_STATE;
      toast.success('Playlist is successful updated');
    });
    builder.addCase(updatePlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      if (action.payload.code === 'MEDIA100003') {
        toast.warn(action.payload.description);
      } else {
        toast.warn('We have some troubles with updating playlist');
      }
    });

    builder.addCase(deletePlaylistThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deletePlaylistThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.playlists = state.data.playlists.filter(
        (playlist) => playlist.id !== action.payload.playlistId
      );
      state.data.userPlaylists = state.data.userPlaylists.filter(
        (playlist) => playlist.id !== action.payload.playlistId
      );
      state.data.likedPlaylists = state.data.likedPlaylists.filter(
        (playlist) => playlist.id !== action.payload.playlistId
      );
      state.data.selectedPlaylist = null;
      state.error = ERROR_INITIAL_STATE;
      toast.success('Playlist is successful deleted');
    });
    builder.addCase(deletePlaylistThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      if (action.payload.code === 'MEDIA100003') {
        toast.warn(action.payload.description);
      } else {
        toast.warn('We have some troubles with deleting playlist');
      }
    });
  }
});

export const { setSelectedPlaylist } = playlistSlice.actions;

export const { reducer: playlistReducer } = playlistSlice;
