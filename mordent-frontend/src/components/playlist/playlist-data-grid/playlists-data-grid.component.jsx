import React, { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  handlePlayPlaylistThunk,
  selectPlayerPlaylist,
  selectPlayerSettings,
  setPlayerSetting
} from '../../../redux/player';
import {
  getUserPlaylistsThunk,
  selectLoadingPlaylistState,
  selectUserPlaylists
} from '../../../redux/playlist';
import PlaylistsDataGridItem from './playlists-data-grid-item.component';
import PlaylistsDataGridMockItem from './playlists-data-grid-mock-item.component';
import { selectJwtState } from '../../../redux/jwt';

const PlaylistsDataGrid = ({ playlists, isCompressed = false }) => {
  const rowRef = useRef(null);
  const isPlaylistsLoading = useSelector(selectLoadingPlaylistState);
  const userPlaylists = useSelector(selectUserPlaylists);
  const playerPlaylist = useSelector(selectPlayerPlaylist);
  const [playlistCount, setPlaylistCount] = useState(null);
  const settings = useSelector(selectPlayerSettings);
  const jwt = useSelector(selectJwtState);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!userPlaylists || userPlaylists.length === 0) {
      dispatch(getUserPlaylistsThunk());
    }
  }, []);

  useEffect(() => {
    if (isCompressed && rowRef.current) {
      const gridWidth = rowRef.current.offsetWidth;
      setPlaylistCount(Math.floor(gridWidth / 225));
    }
  }, [playlists, rowRef, isCompressed]);

  const handlePlayPlaylist = (playlist) => {
    if (playlist.id === playerPlaylist.id) {
      dispatch(setPlayerSetting({ play: !settings.play }));
    } else {
      dispatch(
        handlePlayPlaylistThunk({ playlists, playlistId: playlist.id, songs: playlist.songs })
      );
    }
  };

  return (
    <div
      className={`flex ${
        !isPlaylistsLoading && (!playlists || playlists.length === 0) ? 'flex-col' : 'max-w-max'
      } justify-center items-center`}>
      {!isPlaylistsLoading && (!playlists || playlists.length === 0) ? (
        <div className={'flex justify-center items-center p-1'}>No playlists</div>
      ) : (
        <div
          ref={rowRef}
          style={isCompressed ? {} : { height: 'calc(100vh - 300px)' }}
          className={`flex max-w-max p-1 gap-1.5 ${
            isCompressed ? 'flex-nowrap overflow-hidden' : 'flex-wrap overflow-scroll'
          }`}>
          {isPlaylistsLoading && (!playlists || playlists.length === 0)
            ? new Array(50)
                .fill('')
                .slice(0, playlistCount ? playlistCount : 50)
                .map((_, index) => (
                  <PlaylistsDataGridMockItem
                    key={index}
                    isCompressed={isCompressed}
                    playlistCount={playlistCount}
                  />
                ))
            : playlists
                .slice(0, playlistCount ? playlistCount : playlists.length)
                .map((playlist, index) => (
                  <PlaylistsDataGridItem
                    key={index}
                    playlist={playlist}
                    jwt={jwt}
                    playerPlaylist={playerPlaylist}
                    settings={settings}
                    handlePlayPlaylist={handlePlayPlaylist}
                  />
                ))}
        </div>
      )}
    </div>
  );
};

export default PlaylistsDataGrid;
