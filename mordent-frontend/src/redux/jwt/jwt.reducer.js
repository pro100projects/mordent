import { createSlice } from '@reduxjs/toolkit';

export const JWT_REDUCER_KEY = 'jwt';

const JWT_INITIAL_STATE = {
  data: {
    id: null,
    sub: null,
    username: null,
    roles: null,
    exp: null,
    iat: null,
    enabled: null
  }
};

export const jwtSlice = createSlice({
  name: JWT_REDUCER_KEY,
  initialState: JWT_INITIAL_STATE,
  reducers: {
    setInitialJwtState: (state) => {
      state.data = JWT_INITIAL_STATE.data;
    },
    setJwtState: (state, action) => {
      state.data = action.payload;
    }
  }
});

export const { reducer: jwtReducer } = jwtSlice;

export const { setInitialJwtState, setJwtState } = jwtSlice.actions;
