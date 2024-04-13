import * as React from 'react';
import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import AppBar from '@mui/material/AppBar';
import Container from '@mui/material/Container';
import Toolbar from '@mui/material/Toolbar';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { Button, TextField } from '@mui/material';
import LogoIcon from '../../ui/icons/logo.icon';
import { ROUTES } from '../../App.constants';
import UserDropDawnMenu from './user-drop-dawn-menu.component';
import AddNewDropDawn from './add-new-drop-dawn.component';
import SearchIcon from '@mui/icons-material/Search';
import { hasAnyPermissions, hasPermissions, ROLES } from '../../shared/permissions';
import { useDispatch } from 'react-redux';
import { searchAlbumsThunk } from '../../redux/album';
import { searchPlaylistsThunk } from '../../redux/playlist';
import { searchSongsThunk } from '../../redux/song';

const Header = ({ roles }) => {
  const [search, setSearch] = useState('');

  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleChange = (event) => setSearch(event.target.value);

  const handleKeyDown = (event) => {
    if (event.key === 'Enter' || event.keyCode === 13) {
      if (search.trim().length === 0) {
        navigate(ROUTES.home);
        return;
      }
      dispatch(searchAlbumsThunk(search));
      dispatch(searchPlaylistsThunk(search));
      dispatch(searchSongsThunk(search));
      navigate(ROUTES.search);
    }
  };

  return (
    <AppBar className={'bg-white fixed z-[100]'}>
      <Container maxWidth="lg">
        <Toolbar className={'flex justify-between'} disableGutters>
          <NavLink
            to={hasAnyPermissions(roles) ? ROUTES.home : ROUTES.welcome}
            className={'flex items-center no-underline'}>
            <LogoIcon style={{ marginRight: '5px' }} />
            <Typography
              variant="h5"
              noWrap
              className={'mr-2 text-black font-semibold no-underline'}>
              Mordent
            </Typography>
          </NavLink>

          <Box>
            {hasAnyPermissions(roles) && (
              <TextField
                label="Search"
                size="small"
                margin="normal"
                fullWidth={true}
                value={search}
                onChange={handleChange}
                onKeyDown={handleKeyDown}
                sx={{ m: 1, width: '75ch' }}
                InputProps={{
                  startAdornment: <SearchIcon position="start" />
                }}
              />
            )}
          </Box>

          <Box>
            {hasAnyPermissions(roles) ? (
              <>
                {hasPermissions(roles, [ROLES.ARTIST, ROLES.ADMIN]) && <AddNewDropDawn />}
                <UserDropDawnMenu roles={roles} />
              </>
            ) : (
              <>
                <NavLink to={ROUTES.signIn} className={'no-underline text-[30px] mr-5'}>
                  <Button variant={'contained'}>Sign in</Button>
                </NavLink>
                <NavLink to={ROUTES.signUp} className={'no-underline text-[30px] mr-5'}>
                  <Button variant={'contained'}>Sign up</Button>
                </NavLink>
              </>
            )}
          </Box>
        </Toolbar>
      </Container>
    </AppBar>
  );
};

export default Header;
