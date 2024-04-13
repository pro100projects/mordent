export const ROUTES = {
  home: '/',
  welcome: '/welcome',
  user: '/user',
  search: '/search',
  //song
  song: '/songs/:id',
  songs: '/songs',
  likedSongs: '/liked-songs',
  //playlist
  playlist: '/playlists/:id',
  playlists: '/playlists',
  likedPlaylists: '/liked-playlists',
  //album
  album: '/albums/:id',
  albums: '/albums',
  likedAlbums: '/liked-albums',
  //artist
  artist: '/artists/:id',
  artists: '/artists',
  likedArtists: '/liked-artists',
  //auth
  signIn: '/sign-in',
  signUp: '/sign-up',
  activateRedirect: '/activate-redirect',
  oauth2Redirect: '/oauth2-redirect',
  forgotPassword: '/forgot-password',
  resetPassword: '/reset-password',
  resetPasswordRedirect: '/reset-password-redirect'
};

export const API_ROUTES = {
  //auth-service
  login: '/api/auth/login',
  loginViaGoogle: `/api/auth/oauth2?redirect_uri=${origin}${ROUTES.oauth2Redirect}`,
  forgotPassword: (login) => `/api/auth/forgot-password?login=${login}`,
  resetPassword: '/api/auth/reset-password',
  registration: '/api/auth/registration',
  userInfo: (email, token) => `/api/auth/user?email=${email}&token=${token}`,
  //media-service
  //user
  user: '/api/user',
  userAvatar: '/api/user/avatar',
  userPassword: '/api/user/password',
  //songs
  songs: (songId, name) =>
    `/api/songs${songId ? `?id=${songId}` : ''}${name ? `?name=${name}` : ''}`,
  song: (songId) => `${API_ROUTES.songs()}/${songId}`,
  songStatistic: (songId) => `${API_ROUTES.songs()}/statistic?id=${songId}`,
  likedSongs: '/api/songs/liked',
  toggleLikeSong: (songId) => `/api/songs/toggle-like?id=${songId}`,
  //playlists
  playlists: (playlistId, owned, name) =>
    `/api/playlists${playlistId ? `?id=${playlistId}` : ''}${owned ? `?owned=${owned}` : ''}${
      name ? `?name=${name}` : ''
    }`,
  playlist: (playlistId) => `${API_ROUTES.playlists()}/${playlistId}`,
  playlistStatistic: (playlistId) => `${API_ROUTES.playlists()}/statistic?id=${playlistId}`,
  playlistSongs: (playlistId) => `${API_ROUTES.playlists()}/songs?id=${playlistId}`,
  likedPlaylists: '/api/playlists/liked',
  toggleLikePlaylist: (playlistId) => `/api/playlists/toggle-like?id=${playlistId}`,
  toggleSongPlaylist: (playlistId, songId) =>
    `/api/playlists/toggle-song?playlistId=${playlistId}&songId=${songId}`,
  //albums
  albums: (albumId, owned, name) =>
    `/api/albums${albumId ? `?id=${albumId}` : ''}${owned ? `?owned=${owned}` : ''}${
      name ? `?name=${name}` : ''
    }`,
  album: (albumId) => `${API_ROUTES.albums()}/${albumId}`,
  albumStatistic: (albumId) => `${API_ROUTES.albums()}/statistic?id=${albumId}`,
  albumSongs: (albumId) => `${API_ROUTES.albums()}/songs?id=${albumId}`,
  likedAlbums: '/api/albums/liked',
  toggleLikeAlbum: (albumId) => `/api/albums/toggle-like?id=${albumId}`,
  toggleSongAlbum: (albumId, songId) =>
    `/api/albums/toggle-song?playlistId=${albumId}&songId=${songId}`
};
