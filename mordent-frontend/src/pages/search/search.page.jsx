import React from 'react';
import SongsDataGrid from '../../components/song/song-data-grid/songs-data-grid.component';
import { useSelector } from 'react-redux';
import { selectSearchSongs } from '../../redux/song';
import { defaultSongsColumns } from '../../components/song/song-data-grid/song-data-grid-colums';
import { selectSearchPlaylists } from '../../redux/playlist';
import PlaylistsDataGrid from '../../components/playlist/playlist-data-grid/playlists-data-grid.component';
import { Button, Typography } from '@mui/material';
import { ROUTES } from '../../App.constants';
import { NavLink } from 'react-router-dom';
import AlbumsDataGrid from '../../components/album/album-data-grid/albums-data-grid.component';
import { selectSearchAlbums } from '../../redux/album';

const SearchPage = () => {
  const albums = useSelector(selectSearchAlbums);
  const playlists = useSelector(selectSearchPlaylists);
  const songs = useSelector(selectSearchSongs);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      <Typography variant="h4" className={'pt-5'}>
        Search result
      </Typography>
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

export default SearchPage;
