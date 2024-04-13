import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ROUTES } from '../../App.constants';
import { toast } from 'react-toastify';

const ResetPasswordRedirectHandler = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    if (searchParams.has('email') && searchParams.has('uuid')) {
      const email = searchParams.get('email');
      const uuid = searchParams.get('uuid');
      navigate(ROUTES.resetPassword, {
        state: { email: email, uuid: uuid }
      });
    } else {
      toast.warn('Something went wrong. Please try again');
      navigate(ROUTES.forgotPassword);
    }
  }, []);
};

export default ResetPasswordRedirectHandler;
