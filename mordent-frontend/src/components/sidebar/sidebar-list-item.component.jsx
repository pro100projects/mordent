import React from 'react';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { NavLink } from 'react-router-dom';

import './sidebar.component.scss';

const SidebarListItem = ({ to, icon, text }) => {
  return (
    <ListItem disablePadding>
      <ListItemButton>
        <NavLink to={to} className={'flex no-underline w-full text-black items-center'}>
          <ListItemIcon>{icon}</ListItemIcon>
          <ListItemText primary={text} className={'ml-[-10px]'} />
        </NavLink>
      </ListItemButton>
    </ListItem>
  );
};
export default SidebarListItem;
