import { SONG_REDUCER_KEY } from './song.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[SONG_REDUCER_KEY];

export const selectLoadingSongState = createSelector(selectSlice, (slice) => slice.isLoading);

export const selectSongs = createSelector(selectSlice, (slice) => slice.data.songs);

export const selectUserSongs = createSelector(selectSlice, (slice) => slice.data.userSongs);

export const selectLikedSongs = createSelector(selectSlice, (slice) => slice.data.likedSongs);

export const selectSearchSongs = createSelector(selectSlice, (slice) => slice.data.searchSongs);
