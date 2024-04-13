import { API_ROUTES } from '../App.constants';
import $api from './index';

export const getAlbumsRequest = async (name) => {
  return await $api.get(API_ROUTES.albums(null, false, name));
};

export const getAlbumRequest = async (albumId) => {
  return await $api.get(API_ROUTES.album(albumId));
};

export const getAlbumStatisticRequest = async (albumId) => {
  return await $api.get(API_ROUTES.albumStatistic(albumId));
};

export const getAlbumSongsRequest = async (albumId) => {
  return await $api.get(API_ROUTES.albumSongs(albumId));
};

export const getUserAlbumsRequest = async () => {
  return await $api.get(API_ROUTES.albums(null, true));
};

export const getLikedAlbumsRequest = async () => {
  return await $api.get(API_ROUTES.likedAlbums);
};

export const saveAlbumRequest = async (formData) => {
  return await $api.post(API_ROUTES.albums(), formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const toggleLikeAlbumRequest = async (albumId) => {
  return await $api.post(API_ROUTES.toggleLikeAlbum(albumId));
};

export const toggleSongAlbumRequest = async (albumId, songId) => {
  return await $api.post(API_ROUTES.toggleSongAlbum(albumId, songId));
};

export const updateAlbumRequest = async (formData) => {
  return await $api.put(API_ROUTES.albums(), formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const deleteAlbumRequest = async (albumId) => {
  return await $api.delete(API_ROUTES.albums(albumId));
};
