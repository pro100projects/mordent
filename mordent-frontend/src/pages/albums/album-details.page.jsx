import React, { useEffect, useState } from 'react';
import SongsDataGrid from '../../components/song/song-data-grid/songs-data-grid.component';
import { defaultSongsColumns } from '../../components/song/song-data-grid/song-data-grid-colums';
import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  deleteAlbumThunk,
  getAlbumStatisticThunk,
  getAlbumThunk,
  selectSelectedAlbum,
  setSelectedAlbum,
  toggleLikeAlbumThunk
} from '../../redux/album';
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
import mockAlbumImage from '../../images/mock-album-image.jpg';
import Typography from '@mui/material/Typography';
import { getDurationFromSongs } from '../../shared/data-mapping';
import { PauseRounded, PlayArrowRounded } from '@mui/icons-material';
import {
  handlePlayAlbumThunk,
  selectPlayerAlbum,
  selectPlayerSettings,
  setPlayerSetting
} from '../../redux/player';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import FavoriteIcon from '@mui/icons-material/Favorite';
import FavoriteBorderIcon from '@mui/icons-material/FavoriteBorder';
import { StaticAlbumContextMenu } from '../../components/context-menu/context-menu';
import CustomModal from '../../ui/modal/custom-modal.component';
import AlbumForm from '../../components/album/album-form/album-form.component';
import { selectJwtState } from '../../redux/jwt';
import { ROUTES } from '../../App.constants';
import { hasSameUserIdOrPermissions, ROLES } from '../../shared/permissions';

const AlbumDetailsPage = () => {
  const params = useParams();

  const album = useSelector(selectSelectedAlbum);
  const playerAlbum = useSelector(selectPlayerAlbum);
  const settings = useSelector(selectPlayerSettings);
  const jwt = useSelector(selectJwtState);

  const [openMenu, setOpenMenu] = useState(null);
  const [openModal, setOpenModal] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);
  const [menuCoordinates, setMenuCoordinates] = useState(null);

  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(getAlbumThunk(params.id));
    return () => {
      dispatch(setSelectedAlbum(null));
    };
  }, []);

  const handlePlay = () => {
    if (album.id === playerAlbum.id) {
      dispatch(setPlayerSetting({ play: !settings.play }));
    } else {
      dispatch(handlePlayAlbumThunk({ albumId: album.id, songs: album.songs }));
    }
  };

  const handleLike = () => {
    dispatch(toggleLikeAlbumThunk(album.id));
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
    dispatch(getAlbumStatisticThunk(album.id));
    setOpenDialog(true);
    handleClose();
    handleCloseModal();
  };

  const handleCloseDialog = () => setOpenDialog(false);

  const handleDelete = () => {
    dispatch(deleteAlbumThunk(album.id));
    handleCloseDialog();
    navigate(ROUTES.home);
  };

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      {album && (
        <>
          <Card className={'relative items-center p-1 flex'} onClick={handleOpenModal}>
            <CardMedia className={'p-3'}>
              <img
                src={
                  album.imageFilename
                    ? `${origin}/files/albums/${album.id}${album.imageFilename}`
                    : mockAlbumImage
                }
                alt={album.name}
                onLoad={(e) => {
                  e.target.style.opacity = 1;
                }}
                onError={(e) => {
                  e.target.src = mockAlbumImage;
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
                album
              </Typography>
              <div>
                <Typography variant="h6" component="div" align="left">
                  {album.name}
                </Typography>
                <Typography variant="subtitle1" component="div" align="left" className={'w-3/4'}>
                  {album.description}
                </Typography>
              </div>
              <Typography variant="subtitle2" component="div" align="left">
                {album.songIds.length} {album.songIds.length === 1 ? 'song' : 'songs'}
                {album.songs &&
                  album.songs.length !== 0 &&
                  `, about ${getDurationFromSongs(album.songs)}`}
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
              {album.id === playerAlbum.id && settings.play ? (
                <PauseRounded htmlColor={'#fff'} style={{ fontSize: 32 }} />
              ) : (
                <PlayArrowRounded htmlColor={'#fff'} style={{ fontSize: 32 }} />
              )}
            </IconButton>
            <IconButton sx={{ color: 'red', fontSize: 32 }} onClick={handleLike}>
              {album.liked ? (
                <FavoriteIcon style={{ fontSize: 32 }} />
              ) : (
                <FavoriteBorderIcon style={{ fontSize: 32 }} />
              )}
            </IconButton>
            <IconButton onClick={handleOpen}>
              <MoreHorizIcon htmlColor={'#000'} style={{ fontSize: 32 }} />
            </IconButton>
            <StaticAlbumContextMenu
              openMenu={openMenu}
              menuCoordinates={menuCoordinates}
              handleClose={handleClose}
              album={album}
              jwt={jwt}
              handleOpenModal={handleOpenModal}
              handleOpenDialog={handleOpenDialog}
            />
          </div>
          <SongsDataGrid songs={album?.songs ? album?.songs : []} columns={defaultSongsColumns} />
          {openModal &&
            hasSameUserIdOrPermissions(album.userId, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
              <CustomModal open={openModal} handleClose={handleCloseModal}>
                <AlbumForm
                  album={album}
                  setModalClose={handleCloseModal}
                  handleOpenDialog={handleOpenDialog}
                />
              </CustomModal>
            )}
          {openDialog &&
            hasSameUserIdOrPermissions(album.userId, jwt, [ROLES.ARTIST], [ROLES.ADMIN]) && (
              <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>{`Do you want to delete the album "${album.name}"?`}</DialogTitle>
                <DialogContent>
                  <DialogContentText>
                    This album has {album.songIds.length} songs, {album.songsLikes} song likes and{' '}
                    {album.songsPlaybacks} listens, also liked by {album.likes} users. Are you sure
                    you want to delete it?
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

export default AlbumDetailsPage;
