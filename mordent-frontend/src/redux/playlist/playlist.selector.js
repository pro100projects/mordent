import { PLAYLIST_REDUCER_KEY } from './playlist.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[PLAYLIST_REDUCER_KEY];

export const selectLoadingPlaylistState = createSelector(selectSlice, (slice) => slice.isLoading);

export const selectPlaylists = createSelector(selectSlice, (slice) => slice.data.playlists);

export const selectUserPlaylists = createSelector(selectSlice, (slice) => slice.data.userPlaylists);

export const selectUserPlaylistNames = createSelector(selectSlice, (slice) =>
  slice.data.userPlaylists.map((playlist) => ({ id: playlist.id, name: playlist.name }))
);

export const selectLikedPlaylists = createSelector(
  selectSlice,
  (slice) => slice.data.likedPlaylists
);

export const selectSearchPlaylists = createSelector(
  selectSlice,
  (slice) => slice.data.searchPlaylists
);

export const selectSelectedPlaylist = createSelector(
  selectSlice,
  (slice) => slice.data.selectedPlaylist
);
