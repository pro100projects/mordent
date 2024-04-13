import React from 'react';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import { Divider } from '@mui/material';
import SidebarListItem from './sidebar-list-item.component';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import TimelineRoundedIcon from '@mui/icons-material/TimelineRounded';
import ExploreRoundedIcon from '@mui/icons-material/ExploreRounded';
import AlbumRoundedIcon from '@mui/icons-material/AlbumRounded';
import PlayCircleOutlineRoundedIcon from '@mui/icons-material/PlayCircleOutlineRounded';
import GroupRoundedIcon from '@mui/icons-material/GroupRounded';
import FavoriteRoundedIcon from '@mui/icons-material/FavoriteRounded';
import { ROUTES } from '../../App.constants';

import './sidebar.component.scss';

const Sidebar = () => {
  return (
    <Box className={'sidebar z-[99]'}>
      <List>
        <ListItem disablePadding className={'block'}>
          <SidebarListItem
            to={ROUTES.home}
            icon={<HomeRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Home'}
          />
          <SidebarListItem
            to={ROUTES.home}
            icon={<ExploreRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Discover'}
          />
          <SidebarListItem
            to={ROUTES.home}
            icon={<TimelineRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Trending'}
          />
        </ListItem>
      </List>
      <Divider />
      <List>
        <ListItem disablePadding className={'block'}>
          <SidebarListItem
            to={ROUTES.likedSongs}
            icon={<FavoriteRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Liked Songs'}
          />
          <SidebarListItem
            to={ROUTES.likedPlaylists}
            icon={<PlayCircleOutlineRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Playlists'}
          />
          <SidebarListItem
            to={ROUTES.likedAlbums}
            icon={<AlbumRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Albums'}
          />
          <SidebarListItem
            icon={<GroupRoundedIcon htmlColor={'rgba(0,0,0,0.7)'} />}
            text={'Artists'}
          />
        </ListItem>
      </List>
    </Box>
  );
};
export default Sidebar;
