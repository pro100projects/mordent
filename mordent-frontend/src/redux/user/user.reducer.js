import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { ERROR_INITIAL_STATE, SLICE_INITIAL_STATE } from '../constants';
import {
  deleteUserAvatarRequest,
  deleteUserRequest,
  getUserRequest,
  updateUserAvatarRequest,
  updateUserPasswordRequest,
  updateUserRequest
} from '../../requests/user.requests';

export const USER_REDUCER_KEY = 'user';

const USER_INITIAL_STATE = {
  id: null,
  name: null,
  surname: null,
  username: null,
  email: null,
  avatar: null,
  createdAt: null,
  updatedAt: null,
  enabled: null
};

export const getUserThunk = createAsyncThunk('get user', async (request, { rejectWithValue }) => {
  try {
    const response = await getUserRequest();
    return response.data;
  } catch (error) {
    if (error.error?.code) {
      return rejectWithValue(error);
    }
    return rejectWithValue(error.response?.data?.error);
  }
});

export const updateUserThunk = createAsyncThunk(
  'update user',
  async (request, { rejectWithValue }) => {
    try {
      const response = await updateUserRequest(request);
      //todo: call backend to update tokens if username is new
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const updateUserAvatarThunk = createAsyncThunk(
  'update user avatar',
  async (formData, { rejectWithValue }) => {
    try {
      const response = await updateUserAvatarRequest(formData);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const updateUserPasswordThunk = createAsyncThunk(
  'update user password',
  async (request, { rejectWithValue }) => {
    try {
      const response = await updateUserPasswordRequest(request);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const deleteUserThunk = createAsyncThunk(
  'delete user',
  async (request, { rejectWithValue }) => {
    try {
      const response = await deleteUserRequest();
      //todo: call jwt reducer
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const deleteUserAvatarThunk = createAsyncThunk(
  'delete user avatar',
  async (request, { rejectWithValue }) => {
    try {
      console.log(1);
      const response = await deleteUserAvatarRequest();
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const userSlice = createSlice({
  name: USER_REDUCER_KEY,
  initialState: SLICE_INITIAL_STATE(USER_INITIAL_STATE),
  extraReducers: (builder) => {
    builder.addCase(getUserThunk.pending, (state) => {
      state.isLoading = true;
      state.data = USER_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getUserThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(getUserThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data = USER_INITIAL_STATE;
      state.error = action.payload;
    });

    builder.addCase(updateUserThunk.pending, (state) => {
      state.isLoading = true;
      state.data = USER_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateUserThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = action.payload;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateUserThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data = USER_INITIAL_STATE;
      state.error = action.payload;
    });

    builder.addCase(updateUserAvatarThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateUserAvatarThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = { ...state.data, ...action.payload };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateUserAvatarThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(updateUserPasswordThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateUserPasswordThunk.fulfilled, (state) => {
      state.isLoading = false;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(updateUserPasswordThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(deleteUserThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deleteUserThunk.fulfilled, (state) => {
      state.isLoading = false;
      state.data = USER_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deleteUserThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });

    builder.addCase(deleteUserAvatarThunk.pending, (state) => {
      state.isLoading = true;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deleteUserAvatarThunk.fulfilled, (state) => {
      state.isLoading = false;
      state.data = { ...state.data, avatar: null };
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(deleteUserAvatarThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
    });
  }
});

export const { reducer: userReducer } = userSlice;
