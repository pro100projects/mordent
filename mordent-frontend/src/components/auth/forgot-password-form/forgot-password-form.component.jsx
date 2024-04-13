import React, { useEffect, useState } from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { useForm, useFormState } from 'react-hook-form';
import { Button, Divider, Grid, Typography } from '@mui/material';
import { forgotPasswordThunk } from '../../../redux/auth';
import { CustomTextField } from '../../../ui/custom-fields/custom-outlined-text-field.component';
import { ROUTES } from '../../../App.constants';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';

const ForgotPasswordForm = () => {
  const { state } = useLocation();

  const [type, setType] = useState();

  const { control, handleSubmit, getValues, setValue } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    if (state && state.type && state.login) {
      setType(state.type);
      setValue('login', state.login);
      window.history.replaceState({}, document.title);
    }
  }, []);

  const onChange = () => {
    setType(undefined);
  };

  const onSubmit = (request) => {
    dispatch(forgotPasswordThunk(request.login));
  };

  return (
    <div>
      <Typography variant="h5" component="div" className={'mb-1'}>
        Forgot password
      </Typography>
      <form className="form" onSubmit={handleSubmit(onSubmit)}>
        <Typography variant="body2" component="div" className={'mb-1'}>
          {"Enter your email or username and we'll send you a link to reset password."}
        </Typography>
        <CustomTextField
          control={control}
          name={'login'}
          label={'Login'}
          rules={{ required: 'Login cannot be blank' }}
          onChange={onChange}
          error={errors.login}
        />
        <Button
          type="submit"
          variant="contained"
          fullWidth={true}
          disableElevation={true}
          className={'mt-1'}>
          Send link
        </Button>
      </form>
      <Divider orientation="horizontal" className={'mt-2 mb-2'}>
        <Typography component="div">OR</Typography>
      </Divider>
      <Grid container>
        <Grid item xs={12}>
          <NavLink to={ROUTES.signUp} variant="body2" className="form-link">
            Create new account
          </NavLink>
        </Grid>
      </Grid>
      <Grid container>
        <Grid item xs={12} className={'mt-10'}>
          <Button
            variant="contained"
            fullWidth={true}
            disableElevation={true}
            onClick={() =>
              navigate(ROUTES.signIn, {
                state: { type: type, login: getValues('login') }
              })
            }>
            <ChevronLeftIcon />
            Back to sign in
          </Button>
        </Grid>
      </Grid>
    </div>
  );
};

export default ForgotPasswordForm;
