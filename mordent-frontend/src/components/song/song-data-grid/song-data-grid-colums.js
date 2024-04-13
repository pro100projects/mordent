import { Box, IconButton } from '@mui/material';
import { PauseRounded, PlayArrowRounded } from '@mui/icons-material';
import mockSongImage from '../../../images/mock-music-image.jpeg';
import { dateToString, getDurationFromSeconds } from '../../../shared/data-mapping';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { ROUTES } from '../../../App.constants';
import { NavLink } from 'react-router-dom';
import React from 'react';
import Typography from '@mui/material/Typography';

export const songsColumnTitles = {
  id: '#',
  name: 'Name',
  album: 'Album',
  playback: 'Playback',
  timestamp: 'Date Added',
  like: 'Like',
  duration: 'Duration'
};

export const defaultSongsColumns = (
  hoverSongId,
  playerSong,
  settings,
  handlePlaySong,
  handleLike
) => {
  return [
    {
      field: 'id',
      title: songsColumnTitles.id,
      width: 60,
      render: (song, id) => (
        <div>
          {song.id === hoverSongId || song.id === playerSong.id ? (
            <IconButton onClick={() => handlePlaySong(song)}>
              {song.id === playerSong.id && settings.play ? (
                <PauseRounded htmlColor={'#000'} />
              ) : (
                <PlayArrowRounded htmlColor={'#000'} />
              )}
            </IconButton>
          ) : (
            <div>{id + 1}</div>
          )}
        </div>
      )
    },
    {
      field: 'name',
      title: songsColumnTitles.name,
      width: 250,
      render: (song) => (
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <img
            src={
              song.imageFilename
                ? `${origin}/files/images/${song.id}${song.imageFilename}`
                : mockSongImage
            }
            onLoad={(e) => {
              e.target.style.opacity = 1;
            }}
            onError={(e) => {
              e.target.src = mockSongImage;
            }}
            height={50}
            width={50}
            style={{
              objectFit: 'cover',
              transition: 'opacity 1s ease-in-out',
              opacity: 0
            }}
          />
          <div style={{ marginLeft: 5, textAlign: 'left' }}>
            <Typography>{song.name}</Typography>
            {song.user && (
              <NavLink
                to={`${ROUTES.artists}/${song.user.id}`}
                className={'no-underline text-black hover:underline'}>
                <Typography variant={'subtitle2'}>{song.user.username}</Typography>
              </NavLink>
            )}
          </div>
        </Box>
      )
    },
    {
      field: 'album',
      title: songsColumnTitles.album,
      width: 200,
      render: (song) => (
        <div>
          {song.album && (
            <NavLink
              to={`${ROUTES.albums}/${song.album.id}`}
              className={'no-underline text-black hover:underline'}>
              {song.album.name}
            </NavLink>
          )}
        </div>
      )
    },
    {
      field: 'playback',
      title: songsColumnTitles.playback,
      width: 100,
      render: (song) => <div>{song.playback}</div>
    },
    {
      field: 'createdAt',
      title: songsColumnTitles.timestamp,
      width: 200,
      render: (song) => <div>{dateToString(song.createdAt)}</div>
    },
    {
      field: 'like',
      title: songsColumnTitles.like,
      width: 75,
      render: (song) => (
        <IconButton sx={{ color: 'red' }} onClick={() => handleLike(song.id)}>
          {song.liked ? <FavoriteIcon /> : <FavoriteBorderIcon />}
        </IconButton>
      )
    },
    {
      field: 'duration',
      title: songsColumnTitles.duration,
      width: 150,
      render: (song) => <div>{getDurationFromSeconds(song.metadata?.duration, true)}</div>
    }
  ];
};

export const songsColumnsWithTimestamp = (
  hoverSongId,
  playerSong,
  settings,
  handlePlaySong,
  handleLike
) => {
  return defaultSongsColumns(hoverSongId, playerSong, settings, handlePlaySong, handleLike).map(
    (column) => {
      if (column.title === songsColumnTitles.timestamp) {
        return {
          ...column,
          field: 'timestamp',
          render: (song) => <div>{dateToString(song.timestamp)}</div>
        };
      }
      return column;
    }
  );
};
