import React, { useEffect, useState } from 'react';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Typography } from '@mui/material';
import ImageIcon from '@mui/icons-material/Image';
import MusicNoteIcon from '@mui/icons-material/MusicNote';
import {
  CustomFileTextField,
  CustomSelect,
  CustomTextField
} from '../../../ui/custom-fields/custom-outlined-text-field.component';
import { useDispatch, useSelector } from 'react-redux';
import { saveSongThunk, updateSongThunk } from '../../../redux/song';
import { getUserAlbumsThunk, selectUserAlbums } from '../../../redux/album';

const SongForm = ({ song = null, setModalClose, handleOpenDialog }) => {
  const [image, setImage] = useState(null);
  const [songFile, setSongFile] = useState(null);
  const [error, setError] = useState(null);

  const albums = useSelector(selectUserAlbums);

  const { control, handleSubmit, setValue } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const dispatch = useDispatch();

  useEffect(() => {
    if (song) {
      setValue('name', song.name);
      setValue('albumId', song.album.id);
      setValue('text', song.text);
    }
  }, [song]);

  useEffect(() => {
    if (!albums || albums.length === 0) {
      dispatch(getUserAlbumsThunk());
    }
  }, []);

  const onChangeImage = (e) => {
    setImage(e.target.files[0]);
  };

  const onChangeSong = (e) => {
    setSongFile(e.target.files[0]);
    if (!e.target.files[0]) {
      setError({ message: 'Please select a song' });
    } else {
      setError(null);
    }
  };

  const onSubmit = async (request) => {
    if (song) {
      request = { id: song.id, ...request };
    }
    if (!song && (!songFile || error)) {
      setError({ message: 'Please select a song' });
      return;
    }
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    if (image !== null && image !== undefined) {
      formData.append('image', image, image.name);
    }
    if (songFile !== null && songFile !== undefined) {
      formData.append('song', songFile, songFile.name);
    }

    if (song) {
      dispatch(updateSongThunk(formData));
    } else {
      dispatch(saveSongThunk(formData));
    }
    setModalClose();
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        {song ? 'Edit song' : 'Add new song'}
      </Typography>
      <form className="form" onSubmit={handleSubmit(onSubmit)}>
        <CustomTextField
          control={control}
          name={'name'}
          rules={{ required: 'Name cannot be blank' }}
          label={'Name'}
          error={errors.name}
        />
        <CustomSelect
          control={control}
          name={'albumId'}
          label={'Album'}
          values={albums}
          error={errors.albumId}
        />
        <CustomTextField
          control={control}
          name={'text'}
          label={'Text (optional)'}
          rows={5}
          multiline={true}
          error={errors.text}
        />
        <CustomFileTextField
          label={song ? 'New image' : 'Image'}
          value={image}
          onChange={onChangeImage}
          inputProps={{
            startAdornment: <ImageIcon />
          }}
        />
        {!song && (
          <CustomFileTextField
            label={'Song'}
            value={songFile}
            onChange={onChangeSong}
            error={error}
            inputProps={{
              startAdornment: <MusicNoteIcon />
            }}
          />
        )}
        <Button
          type="submit"
          variant="contained"
          fullWidth={true}
          disableElevation={true}
          className={'mt-1'}>
          {song ? 'Edit song' : 'Create song'}
        </Button>
        {song && (
          <Button
            variant="contained"
            color="secondary"
            fullWidth={true}
            disableElevation={true}
            onClick={handleOpenDialog}
            sx={{ mt: 1 }}>
            Delete song
          </Button>
        )}
      </form>
    </div>
  );
};

export default SongForm;
