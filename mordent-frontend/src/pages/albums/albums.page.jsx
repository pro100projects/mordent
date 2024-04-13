import React, { useEffect } from 'react';
import AlbumsDataGridComponent from '../../components/album/album-data-grid/albums-data-grid.component';
import { useDispatch, useSelector } from 'react-redux';
import { getAlbumsThunk, selectAlbums } from '../../redux/album';

const AlbumsPage = () => {
  const albums = useSelector(selectAlbums);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!albums || albums.length === 0) {
      dispatch(getAlbumsThunk());
    }
  }, []);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      <h1>Albums Page</h1>
      <AlbumsDataGridComponent albums={albums} />
    </div>
  );
};

export default AlbumsPage;
