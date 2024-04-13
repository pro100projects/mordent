import React, { useEffect } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Divider, Grid, Typography } from '@mui/material';
import { resetPasswordThunk, selectAuthStateError } from '../../../redux/auth';
import { CustomPasswordTextField } from '../../../ui/custom-fields/custom-outlined-text-field.component';
import { ROUTES } from '../../../App.constants';
import { handleAuthError } from '../../../helper/error-handler/auth-error.handler';
import { toast } from 'react-toastify';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';

const ResetPasswordForm = () => {
  const { state } = useLocation();

  const { control, handleSubmit, setError, setFocus } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const error = useSelector(selectAuthStateError);

  const { description, type } = handleAuthError(error);
  const dispatch = useDispatch();

  useEffect(() => {
    if (type === 'notification') {
      toast.warn(description);
    }
  }, [description]);

  const onSubmit = (passwords) => {
    if (passwords.password !== passwords.password_repeat) {
      setError('password', { type: 'custom', message: 'Passwords do not match' });
      setError('password_repeat', { type: 'custom', message: 'Passwords do not match' });
      setFocus('password');
      return;
    }
    if (state && state.email && state.uuid) {
      const request = { email: state.email, uuid: state.uuid, password: passwords.password };
      dispatch(resetPasswordThunk(request));
    }
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        Reset password
      </Typography>
      <form className="form" onSubmit={handleSubmit(onSubmit)}>
        <Typography variant="body2" component="div" className={'mb-1'}>
          Please create a new password.
        </Typography>
        <CustomPasswordTextField
          control={control}
          name={'password'}
          label={'New password'}
          error={errors.password}
        />
        <CustomPasswordTextField
          control={control}
          name={'password_repeat'}
          label={'New password repeat'}
          error={errors.password_repeat}
        />
        <Button
          type="submit"
          variant="contained"
          fullWidth={true}
          disableElevation={true}
          sx={{ mt: 1 }}>
          Reset password
        </Button>
      </form>
      <Divider orientation="horizontal" className={'mt-2 mb-2'}>
        <Typography component="div">OR</Typography>
      </Divider>
      <Grid container>
        <Grid item xs={12}>
          <NavLink to={ROUTES.signIn} variant="body2" className="form-link">
            <Button variant="contained" fullWidth={true} disableElevation={true}>
              <ChevronLeftIcon />
              Back to sign in
            </Button>
          </NavLink>
        </Grid>
      </Grid>
    </div>
  );
};

export default ResetPasswordForm;
