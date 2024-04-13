import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { ERROR_INITIAL_STATE, SLICE_INITIAL_STATE } from '../constants';
import { toast } from 'react-toastify';
import {
  deleteAlbumRequest,
  getAlbumRequest,
  getAlbumSongsRequest,
  getAlbumsRequest,
  getAlbumStatisticRequest,
  getLikedAlbumsRequest,
  getUserAlbumsRequest,
  saveAlbumRequest,
  toggleLikeAlbumRequest,
  toggleSongAlbumRequest,
  updateAlbumRequest
} from '../../requests/album.requests';

export const ALBUM_REDUCER_KEY = 'album';

const ALBUM_INITIAL_STATE = {
  albums: [],
  userAlbums: [],
  likedAlbums: [],
  searchAlbums: [],
  selectedAlbum: null
};

export const getAlbumsThunk = createAsyncThunk(
  'get albums',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getAlbumsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const searchAlbumsThunk = createAsyncThunk(
  'search albums',
  async (name, { rejectWithValue }) => {
    try {
      const response = await getAlbumsRequest(name);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getAlbumThunk = createAsyncThunk(
  'get album',
  async (albumId, { dispatch, rejectWithValue }) => {
    try {
      const response = await getAlbumRequest(albumId);
      dispatch(getAlbumSongsThunk(albumId));
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getAlbumStatisticThunk = createAsyncThunk(
  'get album statistic',
  async (albumId, { rejectWithValue }) => {
    try {
      const response = await getAlbumStatisticRequest(albumId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getAlbumSongsThunk = createAsyncThunk(
  'get album songs',
  async (albumId, { rejectWithValue }) => {
    try {
      const response = await getAlbumSongsRequest(albumId);
      return { id: albumId, songs: response.data };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getUserAlbumsThunk = createAsyncThunk(
  'get user albums',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getUserAlbumsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const getLikedAlbumsThunk = createAsyncThunk(
  'get liked albums',
  async (request, { rejectWithValue }) => {
    try {
      const response = await getLikedAlbumsRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const saveAlbumThunk = createAsyncThunk(
  'save album',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await saveAlbumRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const toggleLikeAlbumThunk = createAsyncThunk(
  'toggle like album',
  async (albumId, { rejectWithValue }) => {
    try {
      const response = await toggleLikeAlbumRequest(albumId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const toggleSongAlbumThunk = createAsyncThunk(
  'toggle song album',
  async ({ albumId, songId }, { rejectWithValue }) => {
    try {
      const response = await toggleSongAlbumRequest(albumId, songId);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const updateAlbumThunk = createAsyncThunk(
  'update album',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await updateAlbumRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const deleteAlbumThunk = createAsyncThunk(
  'delete album',
  async (albumId, { rejectWithValue }) => {
    try {
      await deleteAlbumRequest(albumId);
      return { albumId };
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const albumSlice = createSlice({
  name: ALBUM_REDUCER_KEY,
  initialState: SLICE_INITIAL_STATE(ALBUM_INITIAL_STATE),
  reducers: {
    setSelectedAlbum: (state, action) => {
      state.data.selectedAlbum = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(getAlbumsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.albums = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.albums = [];
      state.error = action.payload;
    });

    builder.addCase(searchAlbumsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.searchAlbums = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(searchAlbumsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.searchAlbums = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(searchAlbumsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.searchAlbums = [];
      state.error = action.payload;
    });

    builder.addCase(getAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      if (!state.data.albums.find((album) => album.id === action.payload.id)) {
        state.data.albums = [...state.data.albums, action.payload];
      }
      if (
        action.payload.liked &&
        !state.data.albums.find((album) => album.id === action.payload.id)
      ) {
        state.data.likedAlbums = [...state.data.likedAlbums, action.payload];
      }
      //todo: userAlbums
      state.data.selectedAlbum = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getAlbumStatisticThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumStatisticThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = state.data.albums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, ...action.payload };
        }
        return album;
      });
      state.data.userAlbums = state.data.userAlbums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, ...action.payload };
        }
        return album;
      });
      state.data.likedAlbums = state.data.likedAlbums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, ...action.payload };
        }
        return album;
      });
      if (state.data.selectedAlbum?.id === action.payload.id) {
        state.data.selectedAlbum = { ...state.data.selectedAlbum, ...action.payload };
      }
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumStatisticThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getAlbumSongsThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumSongsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = state.data.albums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, songs: action.payload.songs };
        }
        return album;
      });
      state.data.userAlbums = state.data.userAlbums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, songs: action.payload.songs };
        }
        return album;
      });
      state.data.likedAlbums = state.data.likedAlbums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, songs: action.payload.songs };
        }
        return album;
      });
      state.data.selectedAlbum = { ...state.data.selectedAlbum, songs: action.payload.songs };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getAlbumSongsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(getUserAlbumsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.userAlbums = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getUserAlbumsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.userAlbums = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getUserAlbumsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.userAlbums = [];
      state.error = action.payload;
    });

    builder.addCase(getLikedAlbumsThunk.pending, (state) => {
      state.isLoading = true;
      state.data.likedAlbums = [];
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getLikedAlbumsThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.likedAlbums = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getLikedAlbumsThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data.likedAlbums = [];
      state.error = action.payload;
    });

    builder.addCase(saveAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(saveAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = [...state.data.albums, action.payload];
      state.data.userAlbums = [...state.data.userAlbums, action.payload];
      state.error = ERROR_INITIAL_STATE;
      toast.success('Album is successful added');
    });
    builder.addCase(saveAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      toast.warn('We have some troubles with saving album');
    });

    builder.addCase(toggleLikeAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleLikeAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = state.data.albums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...album, liked: action.payload.liked };
        }
        return album;
      });
      if (action.payload.liked) {
        const album = state.data.albums.find((a) => a.id === action.payload.id);
        state.data.likedAlbums = [...state.data.likedAlbums, album];
      } else {
        state.data.likedAlbums = state.data.likedAlbums.filter(
          (album) => album.id !== action.payload.id
        );
      }
      if (state.data.selectedAlbum?.id === action.payload.id) {
        state.data.selectedAlbum = {
          ...state.data.selectedAlbum,
          liked: action.payload.liked
        };
      }
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleLikeAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(toggleSongAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(toggleSongAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      if (state.data.selectedAlbum?.id === action.payload.albumId) {
        if (!action.payload.saved) {
          state.data.selectedAlbum = {
            ...state.data.selectedAlbum,
            songs: action.payload.songs.filter((song) => song.id !== action.payload.songId)
          };
        }
      }
      //todo: add other
      state.error = ERROR_INITIAL_STATE;
      toast.success('Song is successful added');
    });
    builder.addCase(toggleSongAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      toast.warn('We have some troubles with adding song to album');
    });

    builder.addCase(updateAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = state.data.albums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...action.payload, songs: album.songs };
        }
        return album;
      });
      state.data.userAlbums = state.data.userAlbums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...action.payload, songs: album.songs };
        }
        return album;
      });
      state.data.likedAlbums = state.data.likedAlbums.map((album) => {
        if (album.id === action.payload.id) {
          return { ...action.payload, songs: album.songs };
        }
        return album;
      });
      if (state.data.selectedAlbum?.id === action.payload.id) {
        state.data.selectedAlbum = { ...action.payload, songs: state.data.selectedAlbum.songs };
      }
      state.error = ERROR_INITIAL_STATE;
      toast.success('Album is successful updated');
    });
    builder.addCase(updateAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      if (action.payload.code === 'MEDIA100003') {
        toast.warn(action.payload.description);
      } else {
        toast.warn('We have some troubles with updating album');
      }
    });

    builder.addCase(deleteAlbumThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deleteAlbumThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data.albums = state.data.albums.filter((album) => album.id !== action.payload.albumId);
      state.data.userAlbums = state.data.userAlbums.filter(
        (album) => album.id !== action.payload.albumId
      );
      state.data.likedAlbums = state.data.likedAlbums.filter(
        (album) => album.id !== action.payload.albumId
      );
      state.data.selectedAlbum = null;
      state.error = ERROR_INITIAL_STATE;
      toast.success('Album is successful deleted');
    });
    builder.addCase(deleteAlbumThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      if (action.payload.code === 'MEDIA100003') {
        toast.warn(action.payload.description);
      } else {
        toast.warn('We have some troubles with deleting album');
      }
    });
  }
});

export const { setSelectedAlbum } = albumSlice.actions;

export const { reducer: albumReducer } = albumSlice;
