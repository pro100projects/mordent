import React, { useState } from 'react';
import IconButton from '@mui/material/IconButton';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AddBoxIcon from '@mui/icons-material/AddBox';
import AlbumRoundedIcon from '@mui/icons-material/AlbumRounded';
import PlayCircleOutlineRoundedIcon from '@mui/icons-material/PlayCircleOutlineRounded';
import MusicNoteIcon from '@mui/icons-material/MusicNote';
import Box from '@mui/material/Box';
import CustomModal from '../../ui/modal/custom-modal.component';
import SongForm from '../song/song-form/song-form.component';
import PlaylistForm from '../playlist/playlist-form/playlist-form.component';
import AlbumForm from '../album/album-form/album-form.component';

const items = (handleChangeModal) => [
  {
    title: 'song',
    icon: <MusicNoteIcon sx={{ mr: 1 }} />,
    component: <SongForm setModalClose={handleChangeModal} />
  },
  {
    title: 'playlist',
    icon: <PlayCircleOutlineRoundedIcon sx={{ mr: 1 }} />,
    component: <PlaylistForm setModalClose={handleChangeModal} />
  },
  {
    title: 'album',
    icon: <AlbumRoundedIcon sx={{ mr: 1 }} />,
    component: <AlbumForm setModalClose={handleChangeModal} />
  }
];

const AddNewDropDawn = () => {
  const [openMenu, setOpenMenu] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [children, setChildren] = useState(null);

  const handleOpen = (event) => {
    setOpenMenu(event.currentTarget);
  };

  const handleClose = () => {
    setOpenMenu(null);
  };

  const handleChangeModal = () => {
    setOpenModal((prevState) => !prevState);
  };

  const handleMenuItemClick = (component) => {
    setChildren(component);
    handleClose();
    handleChangeModal();
  };

  return (
    <>
      <IconButton onClick={handleOpen} sx={{ p: 0, mr: 2 }}>
        <AddBoxIcon />
        <ExpandMoreIcon style={{ width: 20 }} />
      </IconButton>
      <Menu
        sx={{ mt: '50px' }}
        id="menu-appbar"
        anchorEl={openMenu}
        anchorOrigin={{
          vertical: 'top',
          horizontal: 'right'
        }}
        keepMounted
        transformOrigin={{
          vertical: 'top',
          horizontal: 'right'
        }}
        open={Boolean(openMenu)}
        onClose={handleClose}>
        <Box sx={{ minWidth: '200px' }}>
          {items(handleChangeModal).map((item, i) => (
            <MenuItem key={i} onClick={() => handleMenuItemClick(item.component)}>
              {item.icon}
              New {item.title}
            </MenuItem>
          ))}
        </Box>
      </Menu>
      {openModal && (
        <CustomModal open={openModal} handleClose={handleChangeModal}>
          {children ? children : <></>}
        </CustomModal>
      )}
    </>
  );
};

export default AddNewDropDawn;
