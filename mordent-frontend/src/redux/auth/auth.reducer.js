import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { ERROR_INITIAL_STATE, SLICE_INITIAL_STATE } from '../constants';
import {
  forgotPasswordRequest,
  loginRequest,
  registrationRequest,
  resetPasswordRequest
} from '../../requests/auth.requests';
import { toast } from 'react-toastify';

export const AUTH_REDUCER_KEY = 'auth';

const AUTH_INITIAL_STATE = {
  successful: false,
  accessToken: null,
  refreshToken: null
};

export const loginThunk = createAsyncThunk('login', async (request, { rejectWithValue }) => {
  try {
    const response = await loginRequest(request);
    return response.data;
  } catch (error) {
    if (error.error?.code) {
      return rejectWithValue(error);
    }
    return rejectWithValue(error.response?.data?.error);
  }
});

//not have reducers
export const forgotPasswordThunk = createAsyncThunk('forgot password', async (login) => {
  try {
    await forgotPasswordRequest(login);
    toast.success('Check your email for a password reset email');
  } catch (error) {
    if (error.error?.code) return;
    if (error.response?.data?.error?.code === 'AUTH100003') {
      toast.warn('This jwt is not registered on our service, please register to get started');
    } else {
      toast.warn('Technical problems. Please contact support');
    }
  }
});

export const resetPasswordThunk = createAsyncThunk(
  'reset password',
  async (request, { rejectWithValue }) => {
    try {
      const response = await resetPasswordRequest(request);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const registrationThunk = createAsyncThunk(
  'registration',
  async (request, { rejectWithValue }) => {
    try {
      const response = await registrationRequest(request);
      return response.data;
    } catch (error) {
      if (error.error?.code) {
        return rejectWithValue(error);
      }
      return rejectWithValue(error.response?.data?.error);
    }
  }
);

export const authSlice = createSlice({
  name: AUTH_REDUCER_KEY,
  initialState: SLICE_INITIAL_STATE(AUTH_INITIAL_STATE),
  reducers: {
    setInitialAuthState: (state) => {
      state.data = AUTH_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    },
    setAuthState: (state, action) => {
      state.isLoading = false;
      state.data = { successful: true, ...action.payload };
      state.error = ERROR_INITIAL_STATE;
    }
  },
  extraReducers: (builder) => {
    builder.addCase(loginThunk.pending, (state) => {
      state.isLoading = true;
      state.data = AUTH_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(loginThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = { successful: true, ...action.payload };
      state.error = ERROR_INITIAL_STATE;
      toast.success('You have successfully logged into your account');
    });
    builder.addCase(loginThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data = AUTH_INITIAL_STATE;
      state.error = action.payload;
    });

    builder.addCase(registrationThunk.pending, (state) => {
      state.isLoading = true;
      state.data = AUTH_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(registrationThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = { successful: true, ...action.payload };
      state.error = ERROR_INITIAL_STATE;
      if (state.data.accessToken) {
        toast.success('You have successfully registered your account');
      } else {
        toast.success('An account activation letter has been sent to your email');
      }
    });
    builder.addCase(registrationThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data = AUTH_INITIAL_STATE;
      state.error = action.payload;
    });

    builder.addCase(resetPasswordThunk.pending, (state) => {
      state.isLoading = true;
      state.data = AUTH_INITIAL_STATE;
      state.error = ERROR_INITIAL_STATE;
    });
    builder.addCase(resetPasswordThunk.fulfilled, (state, action) => {
      state.isLoading = false;
      state.data = { successful: true, ...action.payload };
      state.error = ERROR_INITIAL_STATE;
      toast.success('You have successfully reset your password');
    });
    builder.addCase(resetPasswordThunk.rejected, (state, action) => {
      state.isLoading = false;
      state.data = AUTH_INITIAL_STATE;
      state.error = action.payload;
    });
  }
});

export const { reducer: authReducer } = authSlice;

export const { setInitialAuthState, setAuthState } = authSlice.actions;
