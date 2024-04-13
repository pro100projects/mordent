import React, { useState } from 'react';
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
import { PauseRounded, PlayArrowRounded } from '@mui/icons-material';
import mockPlaylistImage from '../../../images/mock-playlist-image.jpg';
import Typography from '@mui/material/Typography';
import { ROUTES } from '../../../App.constants';
import { NavLink } from 'react-router-dom';
import { PlaylistContextMenu } from '../../context-menu/context-menu';
import CustomModal from '../../../ui/modal/custom-modal.component';
import PlaylistForm from '../playlist-form/playlist-form.component';
import { useDispatch } from 'react-redux';
import { deletePlaylistThunk, getPlaylistStatisticThunk } from '../../../redux/playlist';
import { hasSameUserIdOrPermissions, ROLES } from '../../../shared/permissions';

const PlaylistsDataGridItem = ({ playlist, jwt, playerPlaylist, settings, handlePlayPlaylist }) => {
  const [isHovered, setIsHovered] = useState(false);
  const [contextMenu, setContextMenu] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);

  const dispatch = useDispatch();

  const handleMouseOver = () => {
    setIsHovered(true);
  };

  const handleMouseLeave = () => {
    setIsHovered(false);
  };

  const handleContextMenu = (event) => {
    event.preventDefault();
    setContextMenu(
      contextMenu === null
        ? {
            mouseX: event.clientX + 2,
            mouseY: event.clientY - 6
          }
        : null
    );
  };

  const handleClose = () => {
    setContextMenu(null);
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
  };

  return (
    <Card
      className={'relative items-center p-1 hover:bg-gray-100'}
      onMouseOver={() => handleMouseOver(playlist.id)}
      onMouseLeave={handleMouseLeave}
      onContextMenu={handleContextMenu}
      sx={{ minWidth: 225, maxWidth: 225 }}>
      <NavLink to={`${ROUTES.playlists}/${playlist.id}`} className={'no-underline text-black'}>
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
        <CardContent>
          <Typography variant="h6" component="div" align="left" noWrap sx={{ height: 32 }}>
            {playlist.name}
          </Typography>
        </CardContent>
      </NavLink>
      {(isHovered || playlist.id === playerPlaylist.id) && (
        <IconButton
          onClick={() => handlePlayPlaylist(playlist)}
          style={{
            background: '#0052fd',
            borderRadius: '500px',
            height: 50,
            width: 50,
            bottom: 75,
            right: 10,
            boxShadow: '0 8px 8px rgba(0,0,0,.3)',
            position: 'absolute'
          }}>
          {playlist.id === playerPlaylist.id && settings.play ? (
            <PauseRounded htmlColor={'#fff'} style={{ fontSize: 32 }} />
          ) : (
            <PlayArrowRounded htmlColor={'#fff'} style={{ fontSize: 32 }} />
          )}
        </IconButton>
      )}
      {contextMenu && (
        <PlaylistContextMenu
          contextMenu={contextMenu}
          handleClose={handleClose}
          playlist={playlist}
          jwt={jwt}
          handleOpenModal={handleOpenModal}
          handleOpenDialog={handleOpenDialog}
        />
      )}
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
    </Card>
  );
};

export default PlaylistsDataGridItem;
