import { AUTH_REDUCER_KEY } from './auth.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[AUTH_REDUCER_KEY];

export const selectAuthState = createSelector(selectSlice, (slice) => slice.data);

export const selectAuthStateError = createSelector(selectSlice, (slice) => slice.error);
