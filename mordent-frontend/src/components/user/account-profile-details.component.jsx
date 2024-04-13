import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  Grid
} from '@mui/material';
import { CustomTextField } from '../../ui/custom-fields/custom-outlined-text-field.component';
import { useForm, useFormState } from 'react-hook-form';
import { useDispatch, useSelector } from 'react-redux';
import { selectUserState, updateUserThunk } from '../../redux/user';

const AccountProfileDetails = () => {
  const user = useSelector(selectUserState);
  const [isChanged, setChanged] = useState(false);

  const { control, handleSubmit, setValue } = useForm({
    mode: 'onBlur'
  });
  const { errors } = useFormState({ control });

  const dispatch = useDispatch();

  useEffect(() => {
    setChanged(false);
    setValue('name', user.name);
    setValue('surname', user.surname);
    setValue('username', user.username);
  }, [user]);

  const onChange = () => setChanged(true);

  const onSubmit = async (request) => {
    dispatch(updateUserThunk(request));
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Card>
        <CardHeader title="Profile" subheader="The information can be edited" />
        <CardContent sx={{ pt: 0, height: 145 }}>
          <Box>
            <Grid container spacing={2} justifyContent="center">
              <Grid item xs={12} md={6}>
                <CustomTextField
                  control={control}
                  name={'name'}
                  rules={{ required: 'Name cannot be blank' }}
                  label={'Name'}
                  onChange={onChange}
                  error={errors.name}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <CustomTextField
                  control={control}
                  name={'surname'}
                  rules={{ required: 'Surname cannot be blank' }}
                  label={'Surname'}
                  onChange={onChange}
                  error={errors.surname}
                />
              </Grid>
              <Grid item xs={12} md={6}>
                <CustomTextField
                  control={control}
                  name={'username'}
                  rules={{ required: 'Username cannot be blank' }}
                  label={'Username'}
                  onChange={onChange}
                  error={errors.username}
                />
              </Grid>
            </Grid>
          </Box>
        </CardContent>
        <Divider />
        <CardActions sx={{ justifyContent: 'flex-end' }}>
          <Button type="submit" variant="contained" disabled={!isChanged}>
            Update
          </Button>
        </CardActions>
      </Card>
    </form>
  );
};

export default AccountProfileDetails;
