import { JWT_REDUCER_KEY } from './jwt.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[JWT_REDUCER_KEY];

export const selectJwtState = createSelector(selectSlice, (slice) => slice.data);

export const selectJwtUserId = createSelector(selectSlice, (slice) => slice.data.id);

export const selectJwtRoles = createSelector(selectSlice, (slice) => slice.data.roles);
