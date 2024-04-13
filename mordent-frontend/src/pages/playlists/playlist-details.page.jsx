import React, { useEffect, useState } from 'react';
import SongsDataGrid from '../../components/song/song-data-grid/songs-data-grid.component';
import { songsColumnsWithTimestamp } from '../../components/song/song-data-grid/song-data-grid-colums';
import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  deletePlaylistThunk,
  getPlaylistStatisticThunk,
  getPlaylistThunk,
  selectSelectedPlaylist,
  setSelectedPlaylist,
  toggleLikePlaylistThunk
} from '../../redux/playlist';
import {
  Button,
  Card,
  CardContent,
  CardMedia,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  IconButton
} from '@mui/material';
import mockPlaylistImage from '../../images/mock-playlist-image.jpg';
import Typography from '@mui/material/Typography';
import { getDurationFromSongs } from '../../shared/data-mapping';
import { PauseRounded, PlayArrowRounded } from '@mui/icons-material';
import {
  handlePlayPlaylistThunk,
  selectPlayerPlaylist,
  selectPlayerSettings,
  setPlayerSetting
} from '../../redux/player';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { StaticPlaylistContextMenu } from '../../components/context-menu/context-menu';
import CustomModal from '../../ui/modal/custom-modal.component';
import PlaylistForm from '../../components/playlist/playlist-form/playlist-form.component';
import { selectJwtState } from '../../redux/jwt';
import { ROUTES } from '../../App.constants';
import { hasSameUserIdOrPermissions, ROLES } from '../../shared/permissions';

const PlaylistDetailsPage = () => {
  const params = useParams();

  const playlist = useSelector(selectSelectedPlaylist);
  const playerPlaylist = useSelector(selectPlayerPlaylist);
  const settings = useSelector(selectPlayerSettings);
  const jwt = useSelector(selectJwtState);

  const [openMenu, setOpenMenu] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const [menuCoordinates, setMenuCoordinates] = useState(null);

  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getPlaylistThunk(params.id));
    return () => {
      dispatch(setSelectedPlaylist(null));
    };
  }, []);

  const handlePlay = () => {
    if (playlist.id === playerPlaylist.id) {
      dispatch(setPlayerSetting({ play: !settings.play }));
    } else {
      dispatch(handlePlayPlaylistThunk({ playlistId: playlist.id, songs: playlist.songs }));
    }
  };

  const handleLike = () => {
    dispatch(toggleLikePlaylistThunk(playlist.id));
  };

  const handleOpen = (event) => {
    setOpenMenu(event.currentTarget);
    setMenuCoordinates(event.currentTarget.getBoundingClientRect());
  };

  const handleClose = () => {
    setOpenMenu(null);
    setMenuCoordinates(null);
  };

  const handleOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
  };

  const handleOpenDialog = () => {
    dispatch(getPlaylistStatisticThunk(playlist.id));
    setOpenDialog(true);
    handleClose();
    handleCloseModal();
  };

  const handleCloseDialog = () => setOpenDialog(false);

  const handleDelete = () => {
    dispatch(deletePlaylistThunk(playlist.id));
    handleCloseDialog();
    navigate(ROUTES.home);
  };

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      {playlist && (
        <>
          <Card className={'relative items-center p-1 flex'} onClick={handleOpenModal}>
            <CardMedia className={'p-3'}>
              <img
                src={
                  playlist.imageFilename
                    ? `${origin}/files/playlists/${playlist.id}${playlist.imageFilename}`
                    : mockPlaylistImage
                }
                alt={playlist.name}
                onLoad={(e) => {
                  e.target.style.opacity = 1;
                }}
                onError={(e) => {
                  e.target.src = mockPlaylistImage;
                }}
                height={200}
                width={200}
                style={{
                  borderRadius: 10,
                  objectFit: 'cover',
                  transition: 'opacity 1s ease-in-out',
                  opacity: 0
                }}
              />
            </CardMedia>
            <CardContent className={'flex flex-col justify-between p-0'} sx={{ height: 200 }}>
              <Typography variant="subtitle2" component="div" align="left">
                playlist
              </Typography>
              <div>
                <Typography variant="h6" component="div" align="left">
                  {playlist.name}
                </Typography>
                <Typography variant="subtitle1" component="div" align="left" className={'w-3/4'}>
                  {playlist.description}
                </Typography>
              </div>
              <Typography variant="subtitle2" component="div" align="left">
                {playlist.songIds.length} {playlist.songIds.length === 1 ? 'song' : 'songs'}
                {playlist.songs &&
                  playlist.songs.length !== 0 &&
                  `, about ${getDurationFromSongs(playlist.songs)}`}
              </Typography>
            </CardContent>
          </Card>
          <div className={'flex ml-14 p-2'}>
            <IconButton
              onClick={handlePlay}
              style={{
                background: '#0052fd',
                borderRadius: '500px',
                boxShadow: '0 8px 8px rgba(0,0,0,.3)'
              }}>
              {playlist.id === playerPlaylist.id && settings.play ? (
                <PauseRounded htmlColor={'#fff'} style={{ fontSize: 32 }} />
              ) : (
                <PlayArrowRounded htmlColor={'#fff'} style={{ fontSize: 32 }} />
              )}
            </IconButton>
            <IconButton sx={{ color: 'red', fontSize: 32 }} onClick={handleLike}>
              {playlist.liked ? (
                <FavoriteIcon style={{ fontSize: 32 }} />
              ) : (
                <FavoriteBorderIcon style={{ fontSize: 32 }} />
              )}
            </IconButton>
            <IconButton onClick={handleOpen}>
              <MoreHorizIcon htmlColor={'#000'} style={{ fontSize: 32 }} />
            </IconButton>
            <StaticPlaylistContextMenu
              openMenu={openMenu}
              menuCoordinates={menuCoordinates}
              handleClose={handleClose}
              playlist={playlist}
              jwt={jwt}
              handleOpenModal={handleOpenModal}
              handleOpenDialog={handleOpenDialog}
            />
          </div>
          <SongsDataGrid
            songs={playlist?.songs ? playlist?.songs : []}
            columns={songsColumnsWithTimestamp}
          />
          {openModal &&
            hasSameUserIdOrPermissions(playlist.userId, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
              <CustomModal open={openModal} handleClose={handleCloseModal}>
                <PlaylistForm
                  playlist={playlist}
                  setModalClose={handleCloseModal}
                  handleOpenDialog={handleOpenDialog}
                />
              </CustomModal>
            )}
          {openDialog &&
            hasSameUserIdOrPermissions(playlist.userId, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
              <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>{`Do you want to delete the playlist "${playlist.name}"?`}</DialogTitle>
                <DialogContent>
                  <DialogContentText>
                    This playlist has {playlist.songIds.length} songs also liked by {playlist.likes}{' '}
                    users. Are you sure you want to delete it?
                  </DialogContentText>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleDelete}>Yes</Button>
                  <Button onClick={handleCloseDialog}>No</Button>
                </DialogActions>
              </Dialog>
            )}
        </>
      )}
    </div>
  );
};

export default PlaylistDetailsPage;
