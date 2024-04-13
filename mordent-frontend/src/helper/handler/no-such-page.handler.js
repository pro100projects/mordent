import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../../App.constants';
import { toast } from 'react-toastify';
import { hasAnyPermissions } from '../../shared/permissions';

const NoSuchPageHandler = ({ roles, isLoading }) => {
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoading) {
      if (location.pathname !== ROUTES.home && location.pathname !== ROUTES.welcome) {
        toast.warn('You have followed a not valid link');
      }
      if (hasAnyPermissions(roles)) {
        navigate(ROUTES.home);
      } else {
        navigate(ROUTES.welcome);
      }
    }
  }, [isLoading]);

  return (
    <div className={'pt-[64px] pl-[240px] text-center'}>
      <h1>Not found page. You have followed a not valid link</h1>
    </div>
  );
};

export default NoSuchPageHandler;
