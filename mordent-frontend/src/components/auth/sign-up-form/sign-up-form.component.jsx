import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Divider, Grid, Typography } from '@mui/material';
import {
  CustomCheckBox,
  CustomPasswordTextField,
  CustomTextField
} from '../../../ui/custom-fields/custom-outlined-text-field.component';
import { registrationThunk, selectAuthStateError, setInitialAuthState } from '../../../redux/auth';
import { getUserInfoViaEmailAndGoogleToken } from '../../../requests/auth.requests';
import { handleAuthError } from '../../../helper/error-handler/auth-error.handler';
import { emailValidation } from '../../../helper/validation/auth.validation';
import { API_ROUTES, ROUTES } from '../../../App.constants';
import { toast } from 'react-toastify';

const SignUpForm = () => {
  const { state } = useLocation();

  const { control, handleSubmit, getValues, setValue, setError, setFocus, clearErrors } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const error = useSelector(selectAuthStateError);
  const { description, field, type } = handleAuthError(error);

  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    if (type === 'notification') {
      toast.warn(description);
    }
  }, [description]);

  useEffect(() => {
    if (typeof field === 'string') {
      setError(field, { type: 'custom', message: description });
    } else if (field instanceof Map) {
      //todo: fix error: after set error started cycle re-rendering page
      //field.forEach((value, key) => setError(key, { type: 'custom', message: value }));
    }
  }, [field]);

  useEffect(() => {
    dispatch(setInitialAuthState());
    clearErrors();
    if (state && state.email && state.token) {
      getUserInfoViaEmailAndGoogleToken(state.email, state.token)
        .then((response) => {
          const data = response.data;
          setValue('name', data.name);
          setValue('surname', data.surname);
          setValue('email', data.email);
        })
        .catch((reason) => console.error(reason.response.data.error));
    } else if (state && state.type && state.login) {
      setValue(state.type.toLowerCase(), state.login);
    }
    window.history.replaceState({}, document.title);
  }, []);

  const onSubmit = (user) => {
    if (user.password !== user.repeatPassword) {
      setError('password', { type: 'custom', message: 'Passwords do not match' });
      setError('repeatPassword', { type: 'custom', message: 'Passwords do not match' });
      setFocus('password');
      return;
    }
    if (state && state.email && state.token) {
      const oauth2 = { ...user, token: state.token };
      dispatch(registrationThunk(oauth2));
    } else {
      dispatch(registrationThunk(user));
    }
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        Sign up
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
          name={'surname'}
          rules={{ required: 'Surname cannot be blank' }}
          label={'Surname'}
          error={errors.surname}
        />
        <CustomTextField
          control={control}
          name={'username'}
          rules={{ required: 'Username cannot be blank' }}
          label={'Username'}
          error={errors.username}
        />
        <CustomTextField
          control={control}
          name={'email'}
          rules={emailValidation}
          label={'Email'}
          error={errors.email}
        />
        <CustomPasswordTextField
          control={control}
          name={'password'}
          label={'Password'}
          error={errors.password}
        />
        <CustomPasswordTextField
          control={control}
          name={'repeatPassword'}
          label={'Password repeat'}
          error={errors.repeatPassword}
        />
        <Grid container>
          <Grid item>
            <CustomCheckBox
              control={control}
              name={'artist'}
              label="I'm an artist"
              setValue={setValue}
            />
          </Grid>
        </Grid>
        <Grid container>
          <Grid item>
            <CustomCheckBox
              control={control}
              name={'remember'}
              label="Remember me"
              setValue={setValue}
            />
          </Grid>
        </Grid>
        <Button
          type="submit"
          variant="contained"
          fullWidth={true}
          disableElevation={true}
          className={'mt-2 mb-2'}>
          Sign up
        </Button>
      </form>
      <Divider orientation="horizontal" className={'mt-1 mb-2'}>
        <Typography component="div">OR</Typography>
      </Divider>
      <a type="button" className="google-btn" href={`${origin}${API_ROUTES.loginViaGoogle}`}>
        Sign up with Google
      </a>
      <Grid container>
        <Grid item xs={12} className={'mt-3'}>
          <Typography
            variant="body2"
            className="form-link cursor-pointer"
            onClick={() => {
              const values = getValues();
              const state = values?.email
                ? { type: 'Email', login: values.email }
                : values?.username
                ? { type: 'Username', login: values.username }
                : undefined;
              navigate(ROUTES.signIn, { state });
            }}>
            Have account? Sign in
          </Typography>
        </Grid>
      </Grid>
    </div>
  );
};

export default SignUpForm;
