import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { setAuthState } from '../../redux/auth';
import { ROUTES } from '../../App.constants';
import { toast } from 'react-toastify';

const Oauth2RedirectHandler = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    if (searchParams.has('accessToken') && searchParams.has('refreshToken')) {
      dispatch(
        setAuthState({
          accessToken: searchParams.get('accessToken'),
          refreshToken: searchParams.get('refreshToken')
        })
      );
      toast.success('You have successfully logged into your account');
      navigate(ROUTES.home);
    } else if (searchParams.has('email') && searchParams.has('token')) {
      const email = searchParams.get('email');
      const token = searchParams.get('token');
      navigate(ROUTES.signUp, {
        state: { email: email, token: token }
      });
    } else {
      toast.warn('Google authorization is not successful. Please try again');
    }
  }, []);
};

export default Oauth2RedirectHandler;
