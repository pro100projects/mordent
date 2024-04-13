import React, { useState } from 'react';
import Typography from '@mui/material/Typography';
import { SongContextMenu } from '../../context-menu/context-menu';
import CustomModal from '../../../ui/modal/custom-modal.component';
import SongForm from '../song-form/song-form.component';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';
import { deleteSongThunk, getSongStatisticThunk } from '../../../redux/song';
import { useDispatch } from 'react-redux';
import { hasSameUserIdOrPermissions, ROLES } from '../../../shared/permissions';

const SongsDataGridItem = ({ song, id, jwt, columns, handleMouseOver, handleMouseLeave }) => {
  const [contextMenu, setContextMenu] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);

  const dispatch = useDispatch();

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
    dispatch(getSongStatisticThunk(song.id));
    setOpenDialog(true);
    handleClose();
    handleCloseModal();
  };

  const handleCloseDialog = () => setOpenDialog(false);

  const handleDelete = () => {
    dispatch(deleteSongThunk(song.id));
    handleCloseDialog();
  };

  return (
    <div
      className={'flex items-center p-1 hover:bg-gray-100'}
      onContextMenu={handleContextMenu}
      onMouseOver={() => handleMouseOver(song.id)}
      onMouseLeave={handleMouseLeave}>
      {columns.map((column, index) => (
        <Typography key={index} variant="subtitle1" component="div" style={{ width: column.width }}>
          {column.render(song, id)}
        </Typography>
      ))}
      {contextMenu && (
        <SongContextMenu
          contextMenu={contextMenu}
          handleClose={handleClose}
          song={song}
          jwt={jwt}
          handleOpenModal={handleOpenModal}
          handleOpenDialog={handleOpenDialog}
        />
      )}
      {openModal &&
        hasSameUserIdOrPermissions(song.user.id, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
          <CustomModal open={openModal} handleClose={handleCloseModal}>
            <SongForm
              song={song}
              setModalClose={handleCloseModal}
              handleOpenDialog={handleOpenDialog}
            />
          </CustomModal>
        )}
      {openDialog &&
        hasSameUserIdOrPermissions(song.user.id, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
          <Dialog open={openDialog} onClose={handleCloseDialog}>
            <DialogTitle>{`Do you want to delete the song "${song.name}"?`}</DialogTitle>
            <DialogContent>
              <DialogContentText>
                This song has {song.likes} likes and {song.playback} listens. Are you sure you want
                to delete it?
              </DialogContentText>
            </DialogContent>
            <DialogActions>
              <Button onClick={handleDelete}>Yes</Button>
              <Button onClick={handleCloseDialog}>No</Button>
            </DialogActions>
          </Dialog>
        )}
    </div>
  );
};

export default SongsDataGridItem;
