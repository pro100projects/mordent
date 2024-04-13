import React, { useEffect } from 'react';
import PlaylistsDataGridComponent from '../../components/playlist/playlist-data-grid/playlists-data-grid.component';
import { useDispatch, useSelector } from 'react-redux';
import { getPlaylistsThunk, selectPlaylists } from '../../redux/playlist';

const PlaylistsPage = () => {
  const playlists = useSelector(selectPlaylists);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!playlists || playlists.length === 0) {
      dispatch(getPlaylistsThunk());
    }
  }, []);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      <h1>Playlists Page</h1>
      <PlaylistsDataGridComponent playlists={playlists} />
    </div>
  );
};

export default PlaylistsPage;
