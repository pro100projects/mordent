import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Divider, Grid, InputAdornment, Typography } from '@mui/material';
import {
  CustomCheckBox,
  CustomPasswordTextField,
  CustomTextField
} from '../../../ui/custom-fields/custom-outlined-text-field.component';
import AlternateEmailIcon from '@mui/icons-material/AlternateEmail';
import AccountCircle from '@mui/icons-material/AccountCircle';
import { loginThunk, selectAuthStateError, setInitialAuthState } from '../../../redux/auth';
import { handleAuthError } from '../../../helper/error-handler/auth-error.handler';
import { emailValidation } from '../../../helper/validation/auth.validation';
import { API_ROUTES, ROUTES } from '../../../App.constants';
import { toast } from 'react-toastify';

const SignInForm = () => {
  const { state } = useLocation();
  const [loginField, setLoginField] = useState('Email');

  const { control, handleSubmit, getValues, setValue, setError, unregister, clearErrors } = useForm(
    {
      mode: 'onBlur'
    }
  );
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

  const changeLoginField = () => {
    unregister('login');
    setValue('login', '');

    setLoginField(loginField === 'Email' ? 'Username' : 'Email');
  };

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
    if (state && state.type && state.login) {
      setLoginField(state.type);
      setValue('login', state.login);
      window.history.replaceState({}, document.title);
    }
  }, []);

  const onSubmit = (request) => {
    dispatch(loginThunk(request));
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        Sign in
      </Typography>
      <form className="form" onSubmit={handleSubmit(onSubmit)}>
        <CustomTextField
          control={control}
          name={'login'}
          label={loginField}
          rules={
            loginField === 'Email' ? emailValidation : { required: 'Username cannot be blank' }
          }
          error={errors.login}
          inputProps={{
            startAdornment: (
              <InputAdornment
                title={loginField}
                position={'start'}
                onClick={changeLoginField}
                sx={{ cursor: 'pointer' }}>
                {loginField === 'Email' ? <AlternateEmailIcon /> : <AccountCircle />}
              </InputAdornment>
            )
          }}
        />
        <CustomPasswordTextField
          control={control}
          name={'password'}
          label={'Password'}
          error={errors.password}
        />
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
          className={'mt-1'}>
          Sign in
        </Button>
        <Grid container>
          <Grid item xs={12} className={'flex justify-end'}>
            <Typography
              variant="body2"
              className="form-link cursor-pointer"
              onClick={() =>
                navigate(ROUTES.forgotPassword, {
                  state: { type: loginField, login: getValues('login') }
                })
              }>
              Forgot password?
            </Typography>
          </Grid>
        </Grid>
      </form>
      <Divider orientation="horizontal" className={'mb-2'}>
        <Typography component="div">OR</Typography>
      </Divider>
      <a type="button" className="google-btn" href={`${origin}${API_ROUTES.loginViaGoogle}`}>
        Sign in with Google
      </a>
      <Grid container>
        <Grid item xs={12} className={'mt-3'}>
          <Typography
            variant="body2"
            className="form-link cursor-pointer"
            onClick={() =>
              navigate(ROUTES.signUp, {
                state: { type: loginField, login: getValues('login') }
              })
            }>
            {"Don't have an account? Sign Up"}
          </Typography>
        </Grid>
      </Grid>
    </div>
  );
};

export default SignInForm;
