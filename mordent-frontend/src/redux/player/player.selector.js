import { PLAYER_REDUCER_KEY } from './player.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[PLAYER_REDUCER_KEY];

export const selectPlayerSong = createSelector(selectSlice, (slice) => slice.data.song);

export const selectPlayerAlbum = createSelector(selectSlice, (slice) => slice.data.album);

export const selectPlayerPlaylist = createSelector(selectSlice, (slice) => slice.data.playlist);

export const selectPlayerQueue = createSelector(selectSlice, (slice) => slice.data.queue);

export const selectPlayerSettings = createSelector(selectSlice, (slice) => slice.data.settings);
