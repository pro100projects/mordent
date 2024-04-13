import React, { useEffect, useState } from 'react';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Typography } from '@mui/material';
import ImageIcon from '@mui/icons-material/Image';
import {
  CustomCheckBox,
  CustomFileTextField,
  CustomTextField
} from '../../../ui/custom-fields/custom-outlined-text-field.component';
import { savePlaylistThunk, updatePlaylistThunk } from '../../../redux/playlist';
import { useDispatch } from 'react-redux';

const PlaylistForm = ({ playlist = null, setModalClose, handleOpenDialog }) => {
  const [image, setImage] = useState(null);

  const { control, handleSubmit, setValue } = useForm({
    mode: 'onBlur'
  });

  const { errors } = useFormState({ control });

  const dispatch = useDispatch();

  useEffect(() => {
    if (playlist) {
      setValue('name', playlist.name);
      setValue('description', playlist.description);
    }
  }, [playlist]);

  const onChangeImage = (e) => {
    setImage(e.target.files[0]);
  };

  const onSubmit = async (request) => {
    if (playlist) {
      request = { id: playlist.id, ...request };
    }
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    if (image !== null && image !== undefined) {
      formData.append('image', image, image.name);
    }

    if (playlist) {
      dispatch(updatePlaylistThunk(formData));
    } else {
      dispatch(savePlaylistThunk(formData));
    }
    setModalClose();
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        {playlist ? 'Edit playlist' : 'Add new playlist'}
      </Typography>
      <form className="form" onSubmit={handleSubmit(onSubmit)}>
        <CustomTextField
          control={control}
          name={'name'}
          rules={{ required: 'Name cannot be blank' }}
          label={'Name'}
          error={errors.name}
        />
        <CustomTextField
          control={control}
          name={'description'}
          label={'Description (optional)'}
          rows={5}
          multiline={true}
          error={errors.description}
        />
        <CustomFileTextField
          label={playlist ? 'New image' : 'Image'}
          value={image}
          onChange={onChangeImage}
          inputProps={{
            startAdornment: <ImageIcon />
          }}
        />
        <CustomCheckBox
          control={control}
          name={'isPrivate'}
          label={playlist ? 'Private playlist' : 'Create private playlist'}
          setValue={setValue}
        />
        <Button
          type="submit"
          variant="contained"
          fullWidth={true}
          disableElevation={true}
          className={'mt-1'}>
          {playlist ? 'Edit playlist' : 'Create playlist'}
        </Button>
        {playlist && (
          <Button
            variant="contained"
            color="secondary"
            fullWidth={true}
            disableElevation={true}
            onClick={handleOpenDialog}
            sx={{ mt: 1 }}>
            Delete playlist
          </Button>
        )}
      </form>
    </div>
  );
};

export default PlaylistForm;
