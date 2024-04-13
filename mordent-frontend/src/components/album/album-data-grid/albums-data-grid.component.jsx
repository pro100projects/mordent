import React, { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  handlePlayAlbumThunk,
  selectPlayerAlbum,
  selectPlayerSettings,
  setPlayerSetting
} from '../../../redux/player';
import {
  getUserAlbumsThunk,
  selectLoadingAlbumState,
  selectUserAlbums
} from '../../../redux/album';
import AlbumsDataGridItem from './albums-data-grid-item.component';
import AlbumsDataGridMockItem from './albums-data-grid-mock-item.component';
import { selectJwtState } from '../../../redux/jwt';

const AlbumsDataGrid = ({ albums, isCompressed = false }) => {
  const rowRef = useRef(null);
  const isAlbumsLoading = useSelector(selectLoadingAlbumState);
  const userAlbums = useSelector(selectUserAlbums);
  const playerAlbum = useSelector(selectPlayerAlbum);
  const [albumCount, setAlbumCount] = useState(null);
  const settings = useSelector(selectPlayerSettings);
  const jwt = useSelector(selectJwtState);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!userAlbums || userAlbums.length === 0) {
      dispatch(getUserAlbumsThunk());
    }
  }, []);

  useEffect(() => {
    if (isCompressed && rowRef.current) {
      const gridWidth = rowRef.current.offsetWidth;
      setAlbumCount(Math.floor(gridWidth / 225));
    }
  }, [albums, rowRef, isCompressed]);

  const handlePlayAlbum = (album) => {
    if (album.id === playerAlbum.id) {
      dispatch(setPlayerSetting({ play: !settings.play }));
    } else {
      dispatch(handlePlayAlbumThunk({ albums, albumId: album.id, songs: album.songs }));
    }
  };

  return (
    <div
      className={`flex ${
        !isAlbumsLoading && (!albums || albums.length === 0) ? 'flex-col' : 'max-w-max'
      } justify-center items-center`}>
      {!isAlbumsLoading && (!albums || albums.length === 0) ? (
        <div className={'flex justify-center items-center p-1'}>No albums</div>
      ) : (
        <div
          ref={rowRef}
          style={isCompressed ? {} : { height: 'calc(100vh - 300px)' }}
          className={`flex max-w-max p-1 gap-1.5 ${
            isCompressed ? 'flex-nowrap overflow-hidden' : 'flex-wrap overflow-scroll'
          }`}>
          {isAlbumsLoading && (!albums || albums.length === 0)
            ? new Array(50)
                .fill('')
                .slice(0, albumCount ? albumCount : 50)
                .map((_, index) => (
                  <AlbumsDataGridMockItem
                    key={index}
                    isCompressed={isCompressed}
                    albumCount={albumCount}
                  />
                ))
            : albums
                .slice(0, albumCount ? albumCount : albums.length)
                .map((album, index) => (
                  <AlbumsDataGridItem
                    key={index}
                    album={album}
                    jwt={jwt}
                    playerAlbum={playerAlbum}
                    settings={settings}
                    handlePlayAlbum={handlePlayAlbum}
                  />
                ))}
        </div>
      )}
    </div>
  );
};

export default AlbumsDataGrid;
