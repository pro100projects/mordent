import React from 'react';
import { useNavigate } from 'react-router-dom';
import LogoIcon from '../../ui/icons/logo.icon';
import Typography from '@mui/material/Typography';
import { ROUTES } from '../../App.constants';
import { Button } from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';

import './auth.page.scss';

const AuthPage = ({ children }) => {
  const navigate = useNavigate();
  return (
    <div className="auth-page">
      <div className="component">
        <div className="center h-1/4">
          <div className="flex items-center">
            <Button
              className={'h-5 left-20 absolute'}
              variant={'contained'}
              onClick={() => navigate(-1)}>
              <ChevronLeftIcon />
            </Button>
            <div className={'flex'}>
              <LogoIcon style={{ margin: '5px' }} height={50} width={50} />
              <Typography
                variant="h6"
                noWrap
                component="a"
                href={ROUTES.welcome}
                className={'mr-2 text-black text-5xl font-semibold no-underline'}>
                Mordent
              </Typography>
            </div>
          </div>
        </div>
        <div className="center h-4/6">{children}</div>
      </div>
    </div>
  );
};

export default AuthPage;
