import React, { useEffect, useState } from 'react';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Typography } from '@mui/material';
import ImageIcon from '@mui/icons-material/Image';
import {
  CustomFileTextField,
  CustomTextField
} from '../../../ui/custom-fields/custom-outlined-text-field.component';
import { saveAlbumThunk, updateAlbumThunk } from '../../../redux/album';
import { useDispatch } from 'react-redux';

const AlbumForm = ({ album = null, setModalClose, handleOpenDialog }) => {
  const [image, setImage] = useState(null);

  const { control, handleSubmit, setValue } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const dispatch = useDispatch();

  useEffect(() => {
    if (album) {
      setValue('name', album.name);
      setValue('description', album.description);
    }
  }, [album]);

  const onChangeImage = (e) => {
    setImage(e.target.files[0]);
  };

  const onSubmit = async (request) => {
    if (album) {
      request = { id: album.id, ...request };
    }
    const formData = new FormData();
    formData.append('request', new Blob([JSON.stringify(request)], { type: 'application/json' }));
    if (image !== null && image !== undefined) {
      formData.append('image', image, image.name);
    }

    if (album) {
      dispatch(updateAlbumThunk(formData));
    } else {
      dispatch(saveAlbumThunk(formData));
    }
    setModalClose();
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        {album ? 'Edit album' : 'Add new album'}
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
          label={album ? 'New image' : 'Image'}
          value={image}
          onChange={onChangeImage}
          inputProps={{
            startAdornment: <ImageIcon />
          }}
        />
        <Button
          type="submit"
          variant="contained"
          fullWidth={true}
          disableElevation={true}
          className={'mt-1'}>
          {album ? 'Edit album' : 'Create album'}
        </Button>
        {album && (
          <>
            <Button
              variant="contained"
              color="secondary"
              fullWidth={true}
              disableElevation={true}
              onClick={handleOpenDialog}
              sx={{ mt: 1 }}>
              Delete album
            </Button>
          </>
        )}
      </form>
    </div>
  );
};

export default AlbumForm;
