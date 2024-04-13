import { API_ROUTES } from '../App.constants';
import $api from './index';

export const loginRequest = async (request) => {
  return await $api.post(API_ROUTES.login, request);
};

export const forgotPasswordRequest = async (login) => {
  return await $api.post(API_ROUTES.forgotPassword(login));
};

export const resetPasswordRequest = async (request) => {
  return await $api.post(API_ROUTES.resetPassword, request);
};

export const registrationRequest = async (request) => {
  return await $api.post(API_ROUTES.registration, request);
};

export const getUserInfoViaEmailAndGoogleToken = async (email, token) => {
  return await $api.get(API_ROUTES.userInfo(email, token));
};
