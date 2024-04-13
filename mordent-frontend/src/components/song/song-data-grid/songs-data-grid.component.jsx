import React, { useState } from 'react';
import { Skeleton } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { selectLoadingSongState, toggleLikeSongThunk } from '../../../redux/song';
import {
  selectPlayerSettings,
  selectPlayerSong,
  setPlayerSetting,
  setPlayerSong
} from '../../../redux/player';
import Typography from '@mui/material/Typography';
import SongsDataGridItem from './songs-data-grid-item.component';
import { selectJwtState } from '../../../redux/jwt';

const SongsDataGrid = ({ songs, columns }) => {
  const isSongsLoading = useSelector(selectLoadingSongState);
  const playerSong = useSelector(selectPlayerSong);
  const settings = useSelector(selectPlayerSettings);
  const jwt = useSelector(selectJwtState);

  const [hoverSongId, setHoverSongId] = useState(null);

  const dispatch = useDispatch();

  const handlePlaySong = (song) => {
    if (song.id === playerSong.id) {
      dispatch(setPlayerSetting({ play: !settings.play }));
    } else {
      dispatch(setPlayerSong({ song, play: true }));
    }
  };

  const handleLike = (songId) => {
    dispatch(toggleLikeSongThunk(songId));
  };

  const handleMouseOver = (songId) => {
    setHoverSongId(songId);
  };

  const handleMouseLeave = () => {
    setHoverSongId(null);
  };

  const columnsConfig = columns(hoverSongId, playerSong, settings, handlePlaySong, handleLike);

  return (
    <div className={'flex flex-col justify-center items-center'}>
      <div
        className={'p-2 mb-2 flex flex-wrap items-center'}
        style={{ borderBottom: '1px solid gray' }}>
        {columns(playerSong, settings, handlePlaySong).map((column, index) => (
          <Typography
            key={index}
            variant="subtitle1"
            component="div"
            style={{ width: column.width }}>
            {column.title}
          </Typography>
        ))}
      </div>
      {!isSongsLoading && (!songs || songs.length === 0) ? (
        <Typography
          variant="subtitle1"
          component="div"
          className={'flex justify-center items-center p-1'}>
          No songs
        </Typography>
      ) : (
        <div
          style={{ height: 'calc(100vh - 300px)', marginBottom: '100px' }}
          className={'overflow-scroll'}>
          {isSongsLoading && (!songs || songs.length === 0)
            ? new Array(50).fill('').map((_, i1) => (
                <div key={i1} className={'flex items-center p-1 hover:bg-gray-100'}>
                  {columnsConfig.map((column, i2) => (
                    <Typography
                      key={i2}
                      variant="subtitle1"
                      component="div"
                      style={{ width: column.width, height: 50 }}>
                      <Skeleton sx={{ width: column.width - 10, height: 50 }} />
                    </Typography>
                  ))}
                </div>
              ))
            : songs.map((song, index) => (
                <SongsDataGridItem
                  key={index}
                  song={song}
                  id={index}
                  jwt={jwt}
                  columns={columnsConfig}
                  handleMouseOver={handleMouseOver}
                  handleMouseLeave={handleMouseLeave}
                />
              ))}
        </div>
      )}
    </div>
  );
};

export default SongsDataGrid;
