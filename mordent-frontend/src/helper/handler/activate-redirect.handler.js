import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { setAuthState } from '../../redux/auth';
import { ROUTES } from '../../App.constants';
import { toast } from 'react-toastify';

const ActivateRedirectHandler = () => {
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
      navigate(ROUTES.home);
    } else {
      toast.warn('Account activation failed. Please try again');
      navigate(ROUTES.home);
    }
  }, []);
};

export default ActivateRedirectHandler;
