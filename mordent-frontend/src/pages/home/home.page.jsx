import React, { useEffect } from 'react';
import SongsDataGrid from '../../components/song/song-data-grid/songs-data-grid.component';
import { useDispatch, useSelector } from 'react-redux';
import { getSongsThunk, selectSongs } from '../../redux/song';
import { defaultSongsColumns } from '../../components/song/song-data-grid/song-data-grid-colums';
import { getPlaylistsThunk, selectPlaylists } from '../../redux/playlist';
import PlaylistsDataGrid from '../../components/playlist/playlist-data-grid/playlists-data-grid.component';
import { Button, Typography } from '@mui/material';
import { ROUTES } from '../../App.constants';
import { NavLink } from 'react-router-dom';
import AlbumsDataGrid from '../../components/album/album-data-grid/albums-data-grid.component';
import { getAlbumsThunk, selectAlbums } from '../../redux/album';

const HomePage = () => {
  const albums = useSelector(selectAlbums);
  const playlists = useSelector(selectPlaylists);
  const songs = useSelector(selectSongs);

  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getAlbumsThunk());
    dispatch(getPlaylistsThunk());
    dispatch(getSongsThunk());
  }, []);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      {albums && albums.length !== 0 && (
        <div className={'my-5'}>
          <div className="flex justify-between p-2">
            <Typography variant="h5" component="div">
              Albums
            </Typography>
            <NavLink to={ROUTES.albums}>
              <Button variant="contained" fullWidth={true} disableElevation={true}>
                Show all
              </Button>
            </NavLink>
          </div>
          <AlbumsDataGrid albums={albums} isCompressed={true} />
        </div>
      )}
      {playlists && playlists.length !== 0 && (
        <div className={'my-5'}>
          <div className="flex justify-between p-2">
            <Typography variant="h5" component="div">
              Playlists
            </Typography>
            <NavLink to={ROUTES.playlists}>
              <Button variant="contained" fullWidth={true} disableElevation={true}>
                Show all
              </Button>
            </NavLink>
          </div>
          <PlaylistsDataGrid playlists={playlists} isCompressed={true} />
        </div>
      )}
      {songs && songs.length !== 0 && (
        <div className={'my-5'}>
          <div className="flex justify-between p-2">
            <Typography variant="h5" component="div">
              Songs
            </Typography>
            <NavLink to={ROUTES.songs}>
              <Button variant="contained" fullWidth={true} disableElevation={true}>
                Show all
              </Button>
            </NavLink>
          </div>
          <SongsDataGrid songs={songs} columns={defaultSongsColumns} />
        </div>
      )}
    </div>
  );
};

export default HomePage;
