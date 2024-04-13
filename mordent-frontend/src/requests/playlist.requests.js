import { API_ROUTES } from '../App.constants';
import $api from './index';

export const getPlaylistsRequest = async (name) => {
  return await $api.get(API_ROUTES.playlists(null, false, name));
};

export const getPlaylistRequest = async (playlistId) => {
  return await $api.get(API_ROUTES.playlist(playlistId));
};

export const getPlaylistStatisticRequest = async (playlistId) => {
  return await $api.get(API_ROUTES.playlistStatistic(playlistId));
};

export const getPlaylistSongsRequest = async (playlistId) => {
  return await $api.get(API_ROUTES.playlistSongs(playlistId));
};

export const getUserPlaylistsRequest = async () => {
  return await $api.get(API_ROUTES.playlists(null, true));
};

export const getLikedPlaylistsRequest = async () => {
  return await $api.get(API_ROUTES.likedPlaylists);
};

export const savePlaylistRequest = async (formData) => {
  return await $api.post(API_ROUTES.playlists(), formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const toggleLikePlaylistRequest = async (playlistId) => {
  return await $api.post(API_ROUTES.toggleLikePlaylist(playlistId));
};

export const toggleSongPlaylistRequest = async (playlistId, songId) => {
  return await $api.post(API_ROUTES.toggleSongPlaylist(playlistId, songId));
};

export const updatePlaylistRequest = async (formData) => {
  return await $api.put(API_ROUTES.playlists(), formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const deletePlaylistRequest = async (playlistId) => {
  return await $api.delete(API_ROUTES.playlists(playlistId));
};
