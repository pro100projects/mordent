import { combineReducers } from 'redux';
import { authReducer } from './auth';
import { jwtReducer } from './jwt';
import { userReducer } from './user';
import { songReducer } from './song';
import { playerReducer } from './player';
import { playlistReducer } from './playlist';
import { albumReducer } from './album';

export const rootReducer = combineReducers({
  auth: authReducer,
  jwt: jwtReducer,
  user: userReducer,
  song: songReducer,
  player: playerReducer,
  playlist: playlistReducer,
  album: albumReducer
});
