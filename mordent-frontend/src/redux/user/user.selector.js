import { USER_REDUCER_KEY } from './user.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[USER_REDUCER_KEY];

export const selectLoadingUserState = createSelector(selectSlice, (slice) => slice.isLoading);

export const selectUserState = createSelector(selectSlice, (slice) => slice.data);
