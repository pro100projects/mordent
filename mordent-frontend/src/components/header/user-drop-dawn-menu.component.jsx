import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setInitialAuthState } from '../../redux/auth';
import { setInitialJwtState } from '../../redux/jwt';
import { selectLoadingUserState, selectUserState } from '../../redux/user';
import IconButton from '@mui/material/IconButton';
import Avatar from '@mui/material/Avatar';
import Menu from '@mui/material/Menu';
import Typography from '@mui/material/Typography';
import { Button, Skeleton, Switch } from '@mui/material';
import Divider from '@mui/material/Divider';
import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import Settings from '@mui/icons-material/Settings';
import Logout from '@mui/icons-material/Logout';
import { toast } from 'react-toastify';
import Box from '@mui/material/Box';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ROUTES } from '../../App.constants';
import { NavLink } from 'react-router-dom';
import PersonIcon from '@mui/icons-material/Person';
import { hasPermission, ROLES } from '../../shared/permissions';

const UserDropDawnMenu = ({ roles }) => {
  const [openMenu, setOpenMenu] = useState(null);

  const isLoading = useSelector(selectLoadingUserState);
  const user = useSelector(selectUserState);

  const dispatch = useDispatch();

  const handleOpen = (event) => {
    setOpenMenu(event.currentTarget);
  };

  const handleClose = () => {
    setOpenMenu(null);
  };

  const handleLogout = () => {
    localStorage.clear();
    dispatch(setInitialAuthState());
    dispatch(setInitialJwtState());
    handleClose();
    toast.success('You have successfully logged out of your account');
  };

  return (
    <>
      <IconButton onClick={handleOpen} sx={{ p: 0 }}>
        {isLoading ? (
          <Skeleton variant="circular">
            <Avatar />
          </Skeleton>
        ) : (
          <Avatar
            alt={user.name + ' ' + user.surname}
            src={
              !user.avatar
                ? ''
                : user.avatar.startsWith('http')
                ? user.avatar
                : `${origin}/files/avatars/${user.id}${user.avatar}`
            }
          />
        )}
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
        <Box sx={{ display: 'block', width: '250px', padding: '24px' }}>
          <Box>
            <Box sx={{ display: 'flex', justifyContent: 'center' }}>
              {isLoading ? (
                <Skeleton variant="circular">
                  <Avatar />
                </Skeleton>
              ) : (
                <Avatar
                  alt={user.name + ' ' + user.surname}
                  src={
                    user.avatar && user.avatar.startsWith('http')
                      ? user.avatar
                      : `${origin}/files/avatars/${user.id}${user.avatar}`
                  }
                  sx={{ width: 64, height: 64 }}
                />
              )}
            </Box>
            {isLoading ? (
              <>
                <Skeleton variant="h6" sx={{ mt: 1 }} />
                <Skeleton variant="subtitle1" sx={{ mt: 1, mb: 1 }} />
              </>
            ) : (
              <>
                <Typography
                  variant="h6"
                  noWrap
                  component="div"
                  sx={{ display: 'flex', justifyContent: 'center' }}>
                  {user.name + ' ' + user.surname}
                </Typography>
                <Typography
                  variant="subtitle1"
                  noWrap
                  component="div"
                  sx={{ display: 'flex', justifyContent: 'center' }}>
                  {user.email}
                </Typography>
              </>
            )}
            <Box style={{ display: 'flex', justifyContent: 'center' }}>
              <NavLink to={ROUTES.user}>
                <Button variant="contained" size="large" onClick={handleClose}>
                  Manage your account
                </Button>
              </NavLink>
            </Box>
          </Box>
        </Box>
        <Divider />
        {hasPermission(roles, ROLES.ADMIN) && (
          <MenuItem>
            <ListItemIcon>
              <PersonIcon fontSize="small" />
            </ListItemIcon>
            Admin panel
          </MenuItem>
        )}
        <MenuItem>
          <ListItemIcon>
            <Settings fontSize="small" />
          </ListItemIcon>
          Settings
        </MenuItem>
        <MenuItem>
          <ListItemIcon>
            <Switch sx={{ ml: -1 }} size="small" />
          </ListItemIcon>
          Dark mode
        </MenuItem>
        <MenuItem onClick={handleLogout} sx={{ color: 'red' }}>
          <ListItemIcon>
            <Logout fontSize="small" sx={{ color: 'red' }} />
          </ListItemIcon>
          Sign out
        </MenuItem>
      </Menu>
    </>
  );
};

export default UserDropDawnMenu;
