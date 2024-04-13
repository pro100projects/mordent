import React, { useEffect, useState } from 'react';
import { Route, Routes } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { getUserThunk } from '../redux/user';
import { selectAuthState, setAuthState } from '../redux/auth';
import { selectJwtRoles, setJwtState } from '../redux/jwt';
import jwt from 'jwt-decode';
import { ROUTES } from '../App.constants';
import AuthPage from '../pages/auth/auth.page';
import SignInForm from '../components/auth/sign-in-form/sign-in-form.component';
import SignUpForm from '../components/auth/sign-up-form/sign-up-form.component';
import ForgotPasswordForm from '../components/auth/forgot-password-form/forgot-password-form.component';
import ResetPasswordForm from '../components/auth/reset-password-form/reset-password-form.component';
import ActivateRedirectHandler from '../helper/handler/activate-redirect.handler';
import Oauth2RedirectHandler from '../helper/handler/oauth2-redirect.handler';
import ResetPasswordRedirectHandler from '../helper/handler/reset-password-redirect.handler';
import NoSuchPageHandler from '../helper/handler/no-such-page.handler';
import WelcomePage from '../pages/welcome/welcome.page';
import Header from '../components/header/header.component';
import HomePage from '../pages/home/home.page';
import Sidebar from '../components/sidebar/sidebar.component';
import AudioPlayer from '../components/audio-player/audio-player.component';
import LikeSongsPage from '../pages/songs/like-songs.page';
import PlaylistDetailsPage from '../pages/playlists/playlist-details.page';
import PlaylistsPage from '../pages/playlists/playlists.page';
import LikedPlaylistPage from '../pages/playlists/like-playlists.page';
import UserPage from '../pages/user/user.page';
import LikedAlbumPage from '../pages/albums/like-albums.page';
import AlbumsPage from '../pages/albums/albums.page';
import AlbumDetailsPage from '../pages/albums/album-details.page';
import SearchPage from '../pages/search/search.page';
import { hasAnyPermissions } from '../shared/permissions';

const AppRoutes = () => {
  const authState = useSelector(selectAuthState);
  const roles = useSelector(selectJwtRoles);

  const [isLoading, setLoading] = useState(true);

  const dispatch = useDispatch();

  useEffect(() => {
    try {
      setLoading(false);
      const auth = JSON.parse(localStorage.getItem('auth'));
      if (auth?.successful) {
        if (!authState?.successful || auth.accessToken !== authState.accessToken) {
          dispatch(setAuthState(auth));
          dispatch(setJwtState(jwt(auth.accessToken)));
          dispatch(getUserThunk());
        }
      } else if (authState?.successful) {
        if (!auth?.successful || auth.accessToken !== authState.accessToken) {
          localStorage.setItem('auth', JSON.stringify(authState));
          dispatch(setJwtState(jwt(authState.accessToken)));
          dispatch(getUserThunk());
        }
      }
    } catch (err) {
      console.error(err);
      localStorage.clear();
    }
  }, [authState]);

  return (
    <>
      <Header roles={roles} />
      {hasAnyPermissions(roles) && (
        <>
          <Sidebar />
          <AudioPlayer />
        </>
      )}
      <Routes>
        {hasAnyPermissions(roles) ? (
          <>
            <Route path={ROUTES.home} element={<HomePage />} />
            <Route path={ROUTES.user} element={<UserPage />} />
            <Route path={ROUTES.search} element={<SearchPage />} />
            <Route path={ROUTES.likedSongs} element={<LikeSongsPage />} />
            <Route path={ROUTES.album} element={<AlbumDetailsPage />} />
            <Route path={ROUTES.albums} element={<AlbumsPage />} />
            <Route path={ROUTES.likedAlbums} element={<LikedAlbumPage />} />
            <Route path={ROUTES.playlist} element={<PlaylistDetailsPage />} />
            <Route path={ROUTES.playlists} element={<PlaylistsPage />} />
            <Route path={ROUTES.likedPlaylists} element={<LikedPlaylistPage />} />
          </>
        ) : (
          <>
            <Route path={ROUTES.welcome} element={<WelcomePage />} />
            <Route
              path={ROUTES.signIn}
              element={
                <AuthPage>
                  <SignInForm />
                </AuthPage>
              }
            />
            <Route
              path={ROUTES.signUp}
              element={
                <AuthPage>
                  <SignUpForm />
                </AuthPage>
              }
            />
            <Route
              path={ROUTES.forgotPassword}
              element={
                <AuthPage>
                  <ForgotPasswordForm />
                </AuthPage>
              }
            />
            <Route
              path={ROUTES.resetPassword}
              element={
                <AuthPage>
                  <ResetPasswordForm />
                </AuthPage>
              }
            />
            <Route path={ROUTES.activateRedirect} element={<ActivateRedirectHandler />} />
            <Route path={ROUTES.oauth2Redirect} element={<Oauth2RedirectHandler />} />
            <Route path={ROUTES.resetPasswordRedirect} element={<ResetPasswordRedirectHandler />} />
          </>
        )}
        <Route path="*" element={<NoSuchPageHandler roles={roles} isLoading={isLoading} />} />
      </Routes>
    </>
  );
};

export default AppRoutes;
