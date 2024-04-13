import { API_ROUTES } from '../App.constants';
import $api from './index';

export const getUserRequest = async () => {
  return await $api.get(API_ROUTES.user);
};

export const updateUserRequest = async (request) => {
  return await $api.put(API_ROUTES.user, request);
};

export const updateUserAvatarRequest = async (formData) => {
  return await $api.put(API_ROUTES.userAvatar, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

export const updateUserPasswordRequest = async (request) => {
  return await $api.put(API_ROUTES.userPassword, request);
};

export const deleteUserRequest = async () => {
  return await $api.delete(API_ROUTES.user);
};

export const deleteUserAvatarRequest = async () => {
  return await $api.delete(API_ROUTES.userAvatar);
};
