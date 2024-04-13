import { ALBUM_REDUCER_KEY } from './album.reducer';
import { createSelector } from '@reduxjs/toolkit';

const selectSlice = (state) => state[ALBUM_REDUCER_KEY];

export const selectLoadingAlbumState = createSelector(selectSlice, (slice) => slice.isLoading);

export const selectAlbums = createSelector(selectSlice, (slice) => slice.data.albums);

export const selectUserAlbums = createSelector(selectSlice, (slice) => slice.data.userAlbums);

export const selectUserAlbumNames = createSelector(selectSlice, (slice) =>
  slice.data.userAlbums.map((album) => ({ id: album.id, name: album.name }))
);

export const selectLikedAlbums = createSelector(selectSlice, (slice) => slice.data.likedAlbums);

export const selectSearchAlbums = createSelector(selectSlice, (slice) => slice.data.searchAlbums);

export const selectSelectedAlbum = createSelector(selectSlice, (slice) => slice.data.selectedAlbum);
