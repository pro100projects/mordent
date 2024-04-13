import { API_ROUTES } from '../App.constants';
import $api from './index';

export const getSongsRequest = async (name) => {
  return await $api.get(API_ROUTES.songs(null, name));
};

export const getSongRequest = async (songId) => {
  return await $api.get(API_ROUTES.song(songId));
};

export const getSongStatisticRequest = async (songId) => {
  return await $api.get(API_ROUTES.songStatistic(songId));
};

export const getLikedSongsRequest = async () => {
  return await $api.get(API_ROUTES.likedSongs);
};

export const saveSongRequest = async (formData) => {
  return await $api.post(API_ROUTES.songs(), formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const listenSongRequest = async (songId) => {
  return await $api.post(API_ROUTES.song(songId));
};

export const toggleLikeSongRequest = async (songId) => {
  return await $api.post(API_ROUTES.toggleLikeSong(songId));
};

export const updateSongRequest = async (formData) => {
  return await $api.put(API_ROUTES.songs(), formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const deleteSongRequest = async (songId) => {
  return await $api.delete(API_ROUTES.songs(songId));
};
