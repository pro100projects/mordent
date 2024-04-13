import React, { useEffect } from 'react';
import SongsDataGrid from '../../components/song/song-data-grid/songs-data-grid.component';
import { useDispatch, useSelector } from 'react-redux';
import { getLikedSongsThunk, selectLikedSongs } from '../../redux/song';
import { songsColumnsWithTimestamp } from '../../components/song/song-data-grid/song-data-grid-colums';

const LikeSongsPage = () => {
  const songs = useSelector(selectLikedSongs);

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getLikedSongsThunk());
  }, []);

  return (
    <div className={'pt-[64px] pl-[225px] text-center'}>
      <h1>Liked Songs</h1>
      <SongsDataGrid songs={songs} columns={songsColumnsWithTimestamp} />
    </div>
  );
};

export default LikeSongsPage;
