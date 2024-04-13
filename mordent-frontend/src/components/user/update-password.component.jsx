import React from 'react';
import { CustomPasswordTextField } from '../../ui/custom-fields/custom-outlined-text-field.component';
import { useForm, useFormState } from 'react-hook-form';
import { useDispatch } from 'react-redux';
import { Button, Card, CardActions, CardContent, CardHeader, Divider, Stack } from '@mui/material';
import { updateUserPasswordThunk } from '../../redux/user';

const UpdatePassword = () => {
  const { control, handleSubmit, setError, setFocus } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const dispatch = useDispatch();

  const onSubmit = (request) => {
    if (request.newPassword !== request.repeatPassword) {
      setError('newPassword', { type: 'custom', message: 'Passwords do not match' });
      setError('repeatPassword', { type: 'custom', message: 'Passwords do not match' });
      setFocus('newPassword');
      return;
    }
    dispatch(updateUserPasswordThunk(request));
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Card>
        <CardHeader subheader="Update password" title="Password" />
        <Divider />
        <CardContent>
          <Stack spacing={3} sx={{ maxWidth: 400 }}>
            <CustomPasswordTextField
              control={control}
              name={'oldPassword'}
              label={'Old password'}
              error={errors.oldPassword}
            />
            <CustomPasswordTextField
              control={control}
              name={'newPassword'}
              label={'New password'}
              error={errors.newPassword}
            />
            <CustomPasswordTextField
              control={control}
              name={'repeatPassword'}
              label={'Password repeat'}
              error={errors.repeatPassword}
            />
          </Stack>
        </CardContent>
        <Divider />
        <CardActions sx={{ justifyContent: 'flex-end' }}>
          <Button type="submit" variant="contained">
            Update
          </Button>
        </CardActions>
      </Card>
    </form>
  );
};

export default UpdatePassword;
