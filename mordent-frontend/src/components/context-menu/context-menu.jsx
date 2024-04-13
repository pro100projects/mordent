import React, { useRef, useState } from 'react';
import { Divider, InputAdornment, Menu, MenuItem } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import {
  selectUserPlaylistNames,
  toggleLikePlaylistThunk,
  toggleSongPlaylistThunk
} from '../../redux/playlist';
import { TextFieldNotControlled } from '../../ui/custom-fields/custom-outlined-text-field.component';
import ArrowRightIcon from '@mui/icons-material/ArrowRight';
import SearchIcon from '@mui/icons-material/Search';
import ClearIcon from '@mui/icons-material/Clear';
import { toggleLikeSongThunk } from '../../redux/song';
import { toggleLikeAlbumThunk } from '../../redux/album';
import { hasSameUserIdOrPermissions, ROLES } from '../../shared/permissions';

export const SongContextMenu = ({
  contextMenu,
  handleClose,
  song,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  const playlistsMenuItem = useRef(null);
  const shareMenuItem = useRef(null);
  const [playlistsContextMenu, setPlaylistsContextMenu] = useState(null);
  const [shareContextMenu, setShareContextMenu] = useState(null);

  const dispatch = useDispatch();

  const handlePlaylistsContextMenu = (event) => {
    event.preventDefault();
    setShareContextMenu(null);
    setPlaylistsContextMenu(
      playlistsContextMenu
        ? null
        : {
            mouseX: contextMenu.mouseX + playlistsMenuItem.current.offsetWidth,
            mouseY: event.clientY
          }
    );
  };

  const handleClosePlaylistsContextMenu = () => {
    setPlaylistsContextMenu(null);
  };

  const handleShareContextMenu = (event) => {
    event.preventDefault();
    setPlaylistsContextMenu(null);
    setShareContextMenu(
      shareContextMenu
        ? null
        : {
            mouseX: contextMenu.mouseX + shareMenuItem.current.offsetWidth,
            mouseY: event.clientY
          }
    );
  };

  const handleCloseShareContextMenu = () => {
    setShareContextMenu(null);
  };

  const handleLike = () => {
    handleClose();
    dispatch(toggleLikeSongThunk(song.id));
  };

  const handleEdit = () => {
    handleClose();
    handleOpenModal();
  };

  return (
    <Menu
      open={contextMenu}
      onClose={handleClose}
      anchorReference="anchorPosition"
      anchorPosition={
        contextMenu ? { top: contextMenu.mouseY, left: contextMenu.mouseX } : undefined
      }>
      <div style={{ padding: 5, width: 225 }}>
        {/*todo*/}
        <MenuItem onClick={handleClose}>Add to queue</MenuItem>
        <MenuItem onClick={handleClose}>Report</MenuItem>
        <Divider />
        <MenuItem onClick={handleClose}>Go to artist</MenuItem>
        <MenuItem onClick={handleClose}>Go to album</MenuItem>
        <MenuItem onClick={handleClose}>Show credits</MenuItem>
        <Divider />
        <MenuItem onClick={handleLike}>
          {song.liked ? 'Remove from Liked Songs' : 'Save to your Liked Songs'}
        </MenuItem>
        <MenuItem ref={playlistsMenuItem} onMouseOver={handlePlaylistsContextMenu}>
          Add to playlist
        </MenuItem>
        <MenuItem ref={shareMenuItem} onMouseOver={handleShareContextMenu}>
          <div style={{ width: 225, display: 'flex', justifyContent: 'space-between' }}>
            <div>Share</div>
            <div>
              <ArrowRightIcon />
            </div>
          </div>
        </MenuItem>
        {hasSameUserIdOrPermissions(song.user.id, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
          <>
            <Divider />
            <MenuItem onClick={handleEdit}>Edit song</MenuItem>
            <MenuItem onClick={handleOpenDialog}>Delete song</MenuItem>
          </>
        )}
      </div>
      {playlistsContextMenu && (
        <AddToPlaylistContextMenu
          contextMenu={playlistsContextMenu}
          handleClose={handleClosePlaylistsContextMenu}
          songId={song.id}
        />
      )}
      {shareContextMenu && (
        <ShareContextMenu
          contextMenu={shareContextMenu}
          handleClose={handleCloseShareContextMenu}
        />
      )}
    </Menu>
  );
};

const AlbumContextMenuItems = ({
  contextMenu,
  menuCoordinates,
  handleClose,
  album,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  const shareMenuItem = useRef(null);
  const [shareContextMenu, setShareContextMenu] = useState(null);

  const dispatch = useDispatch();

  const handleShareContextMenu = (event) => {
    event.preventDefault();
    if (contextMenu) {
      setShareContextMenu(
        shareContextMenu
          ? null
          : {
              mouseX: contextMenu.mouseX + shareMenuItem.current.offsetWidth,
              mouseY: event.clientY
            }
      );
    } else {
      setShareContextMenu(
        shareContextMenu
          ? null
          : {
              mouseX: menuCoordinates.x + shareMenuItem.current.offsetWidth,
              mouseY: event.clientY
            }
      );
    }
  };

  const handleCloseShareContextMenu = () => {
    setShareContextMenu(null);
  };

  const handleLike = () => {
    handleClose();
    dispatch(toggleLikeAlbumThunk(album.id));
  };

  const handleEdit = () => {
    handleClose();
    handleOpenModal();
  };

  return (
    <>
      <div style={{ padding: 5, width: 225 }}>
        <MenuItem onClick={handleClose}>Add to queue</MenuItem>
        <MenuItem onClick={handleClose}>Report</MenuItem>
        <MenuItem onClick={handleClose}>Go to owner</MenuItem>
        <MenuItem onClick={handleLike}>
          {album.liked ? 'Remove from your Library' : 'Add to your Library'}
        </MenuItem>
        <Divider />
        <MenuItem ref={shareMenuItem} onMouseOver={handleShareContextMenu}>
          <div style={{ width: 225, display: 'flex', justifyContent: 'space-between' }}>
            <div>Share</div>
            <div>
              <ArrowRightIcon />
            </div>
          </div>
        </MenuItem>
        {hasSameUserIdOrPermissions(album.userId, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
          <>
            <Divider />
            <MenuItem onClick={handleEdit}>Edit album</MenuItem>
            <MenuItem onClick={handleOpenDialog}>Delete album</MenuItem>
          </>
        )}
      </div>
      {shareContextMenu && (
        <ShareContextMenu
          contextMenu={shareContextMenu}
          handleClose={handleCloseShareContextMenu}
        />
      )}
    </>
  );
};

export const AlbumContextMenu = ({
  contextMenu,
  handleClose,
  album,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  return (
    <Menu
      open={contextMenu}
      onClose={handleClose}
      anchorReference="anchorPosition"
      anchorPosition={
        contextMenu ? { top: contextMenu.mouseY, left: contextMenu.mouseX } : undefined
      }>
      <AlbumContextMenuItems
        contextMenu={contextMenu}
        handleClose={handleClose}
        album={album}
        jwt={jwt}
        handleOpenModal={handleOpenModal}
        handleOpenDialog={handleOpenDialog}
      />
    </Menu>
  );
};

export const StaticAlbumContextMenu = ({
  openMenu,
  menuCoordinates,
  handleClose,
  album,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  return (
    <Menu
      sx={{ mt: '32px' }}
      id="menu-appbar"
      anchorEl={openMenu}
      anchorOrigin={{
        vertical: 'top',
        horizontal: 'left'
      }}
      keepMounted
      transformOrigin={{
        vertical: 'top',
        horizontal: 'left'
      }}
      open={Boolean(openMenu)}
      onClose={handleClose}>
      <AlbumContextMenuItems
        menuCoordinates={menuCoordinates}
        handleClose={handleClose}
        album={album}
        jwt={jwt}
        handleOpenModal={handleOpenModal}
        handleOpenDialog={handleOpenDialog}
      />
    </Menu>
  );
};

const PlaylistContextMenuItems = ({
  contextMenu,
  menuCoordinates,
  handleClose,
  playlist,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  const shareMenuItem = useRef(null);
  const [shareContextMenu, setShareContextMenu] = useState(null);

  const dispatch = useDispatch();

  const handleShareContextMenu = (event) => {
    event.preventDefault();
    if (contextMenu) {
      setShareContextMenu(
        shareContextMenu
          ? null
          : {
              mouseX: contextMenu.mouseX + shareMenuItem.current.offsetWidth,
              mouseY: event.clientY
            }
      );
    } else {
      setShareContextMenu(
        shareContextMenu
          ? null
          : {
              mouseX: menuCoordinates.x + shareMenuItem.current.offsetWidth,
              mouseY: event.clientY
            }
      );
    }
  };

  const handleCloseShareContextMenu = () => {
    setShareContextMenu(null);
  };

  const handleLike = () => {
    handleClose();
    dispatch(toggleLikePlaylistThunk(playlist.id));
  };

  const handleEdit = () => {
    handleClose();
    handleOpenModal();
  };

  return (
    <>
      <div style={{ padding: 5, width: 225 }}>
        <MenuItem onClick={handleClose}>Add to queue</MenuItem>
        <MenuItem onClick={handleClose}>Report</MenuItem>
        <MenuItem onClick={handleClose}>Go to owner</MenuItem>
        <MenuItem onClick={handleLike}>
          {playlist.liked ? 'Remove from your Library' : 'Add to your Library'}
        </MenuItem>
        <Divider />
        <MenuItem ref={shareMenuItem} onMouseOver={handleShareContextMenu}>
          <div style={{ width: 225, display: 'flex', justifyContent: 'space-between' }}>
            <div>Share</div>
            <div>
              <ArrowRightIcon />
            </div>
          </div>
        </MenuItem>
        {hasSameUserIdOrPermissions(playlist.userId, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
          <>
            <Divider />
            <MenuItem onClick={handleEdit}>Edit playlist</MenuItem>
            <MenuItem onClick={handleOpenDialog}>Delete playlist</MenuItem>
          </>
        )}
      </div>
      {shareContextMenu && (
        <ShareContextMenu
          contextMenu={shareContextMenu}
          handleClose={handleCloseShareContextMenu}
        />
      )}
    </>
  );
};

export const PlaylistContextMenu = ({
  contextMenu,
  handleClose,
  playlist,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  return (
    <Menu
      open={contextMenu}
      onClose={handleClose}
      anchorReference="anchorPosition"
      anchorPosition={
        contextMenu ? { top: contextMenu.mouseY, left: contextMenu.mouseX } : undefined
      }>
      <PlaylistContextMenuItems
        contextMenu={contextMenu}
        handleClose={handleClose}
        playlist={playlist}
        jwt={jwt}
        handleOpenModal={handleOpenModal}
        handleOpenDialog={handleOpenDialog}
      />
    </Menu>
  );
};

export const StaticPlaylistContextMenu = ({
  openMenu,
  menuCoordinates,
  handleClose,
  playlist,
  jwt,
  handleOpenModal,
  handleOpenDialog
}) => {
  return (
    <Menu
      sx={{ mt: '32px' }}
      id="menu-appbar"
      anchorEl={openMenu}
      anchorOrigin={{
        vertical: 'top',
        horizontal: 'left'
      }}
      keepMounted
      transformOrigin={{
        vertical: 'top',
        horizontal: 'left'
      }}
      open={Boolean(openMenu)}
      onClose={handleClose}>
      <PlaylistContextMenuItems
        menuCoordinates={menuCoordinates}
        handleClose={handleClose}
        playlist={playlist}
        jwt={jwt}
        handleOpenModal={handleOpenModal}
        handleOpenDialog={handleOpenDialog}
      />
    </Menu>
  );
};

export const ShareContextMenu = ({ contextMenu, handleClose }) => {
  return (
    <Menu
      open={contextMenu}
      onClose={handleClose}
      anchorReference="anchorPosition"
      anchorPosition={
        contextMenu ? { top: contextMenu.mouseY, left: contextMenu.mouseX } : undefined
      }>
      {/*todo*/}
      <MenuItem onClick={handleClose}>Copy link</MenuItem>
      <MenuItem onClick={handleClose}>Share</MenuItem>
    </Menu>
  );
};

const AddToPlaylistContextMenu = ({ contextMenu, handleClose, songId }) => {
  const playlists = useSelector(selectUserPlaylistNames);
  const [filteredPlaylists, setFilteredPlaylists] = useState(playlists);
  const [filter, setFilter] = useState('');

  const dispatch = useDispatch();

  const handleChangeSearch = (e) => {
    const filter = e.target.value;
    setFilter(filter);
    setFilteredPlaylists(playlists.filter((playlist) => playlist.name.includes(filter.trim())));
  };

  const handleClearSearch = () => {
    setFilter('');
    setFilteredPlaylists(playlists);
  };

  const handleAddToPlaylist = (playlistId) => {
    dispatch(toggleSongPlaylistThunk({ playlistId, songId }));
  };

  return (
    <Menu
      open={contextMenu}
      onClose={handleClose}
      anchorReference="anchorPosition"
      anchorPosition={
        contextMenu ? { top: contextMenu.mouseY, left: contextMenu.mouseX } : undefined
      }>
      <div style={{ padding: 5, width: 250, overflow: 'hidden' }}>
        <TextFieldNotControlled
          placeholder={'Find a playlist'}
          value={filter}
          onChange={handleChangeSearch}
          inputProps={{
            startAdornment: (
              <InputAdornment position={'start'} sx={{ cursor: 'pointer' }}>
                <SearchIcon />
              </InputAdornment>
            ),
            endAdornment: filter && filter !== '' && (
              <InputAdornment
                onClick={handleClearSearch}
                position={'end'}
                sx={{ cursor: 'pointer' }}>
                <ClearIcon />
              </InputAdornment>
            )
          }}
        />
        <MenuItem>Create playlist</MenuItem>
        <Divider />
        <div style={{ height: 200, maxWidth: 240, overflowX: 'hidden', overflow: 'scroll' }}>
          {filteredPlaylists.map((playlist, index) => (
            <MenuItem key={index} onClick={() => handleAddToPlaylist(playlist.id)}>
              {playlist.name}
            </MenuItem>
          ))}
        </div>
      </div>
    </Menu>
  );
};
