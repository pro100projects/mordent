import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { deleteUserAvatarThunk, selectUserState, updateUserAvatarThunk } from '../../redux/user';
import {
  Avatar,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Divider,
  Typography
} from '@mui/material';
import { dateToString } from '../../shared/data-mapping';

const AccountProfile = () => {
  const user = useSelector(selectUserState);

  const dispatch = useDispatch();

  const onChangeAvatar = (e) => {
    const avatar = e.target.files[0];
    if (avatar) {
      const formData = new FormData();
      formData.append('avatar', avatar, avatar.name);
      dispatch(updateUserAvatarThunk(formData));
    }
  };

  const onDeleteAvatar = () => {
    console.log(2);
    dispatch(deleteUserAvatarThunk());
    console.log(3);
  };

  return (
    <Card>
      <CardContent sx={{ height: 200 }}>
        <Box
          sx={{
            alignItems: 'center',
            display: 'flex',
            flexDirection: 'column'
          }}>
          <Avatar
            src={
              !user.avatar
                ? ''
                : user.avatar.startsWith('http')
                ? user.avatar
                : `${origin}/files/avatars/${user.id}${user.avatar}`
            }
            sx={{
              height: 80,
              mb: 2,
              width: 80
            }}
          />
          <Typography gutterBottom variant="h5">
            {user.name} {user.surname}
          </Typography>
          <Typography color="text.secondary" variant="body2">
            {user.username} {user.email}
          </Typography>
          <Typography color="text.secondary" variant="body2">
            {dateToString(user.createdAt)}
          </Typography>
        </Box>
      </CardContent>
      <Divider />
      <CardActions>
        <Button fullWidth variant="text" component="label">
          Upload picture
          <input hidden accept="image/*" type="file" onChange={onChangeAvatar} />
        </Button>
        <Button fullWidth variant="text" color="secondary" onClick={() => onDeleteAvatar()}>
          Delete picture
        </Button>
      </CardActions>
    </Card>
  );
};

export default AccountProfile;
