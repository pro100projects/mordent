import React, { useEffect } from 'react';
import PlaylistsDataGridComponent from '../../components/playlist/playlist-data-grid/playlists-data-grid.component';
import { useDispatch, useSelector } from 'react-redux';
import { getLikedPlaylistsThunk, selectLikedPlaylists } from '../../redux/playlist';

const LikedPlaylistPage = () => {
  const playlists = useSelector(selectLikedPlaylists);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!playlists || playlists.length === 0) {
      dispatch(getLikedPlaylistsThunk());
    }
  }, []);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      <h1>Liked Playlists Page</h1>
      <PlaylistsDataGridComponent playlists={playlists} />
    </div>
  );
};

export default LikedPlaylistPage;
