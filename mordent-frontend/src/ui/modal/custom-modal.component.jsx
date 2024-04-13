import React from 'react';
import { IconButton, Modal } from '@mui/material';
import Box from '@mui/material/Box';
import CloseIcon from '@mui/icons-material/Close';

const style = {
  position: 'fixed',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  bgcolor: 'background.paper',
  boxShadow: 20,
  p: 5
};

const CustomModal = ({ open, handleClose, children }) => {
  return (
    <Modal open={open} onClose={handleClose}>
      <Box sx={style}>
        <IconButton
          sx={{ position: 'absolute', top: 30, right: 30 }}
          size="small"
          onClick={handleClose}>
          <CloseIcon />
        </IconButton>
        <div style={{ marginTop: 10 }}>{children}</div>
      </Box>
    </Modal>
  );
};

export default CustomModal;
