import React, { useEffect } from 'react';
import AlbumsDataGridComponent from '../../components/album/album-data-grid/albums-data-grid.component';
import { useDispatch, useSelector } from 'react-redux';
import { getLikedAlbumsThunk, selectLikedAlbums } from '../../redux/album';

const LikedAlbumPage = () => {
  const albums = useSelector(selectLikedAlbums);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!albums || albums.length === 0) {
      dispatch(getLikedAlbumsThunk());
    }
  }, []);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      <h1>Liked Albums Page</h1>
      <AlbumsDataGridComponent albums={albums} />
    </div>
  );
};

export default LikedAlbumPage;
