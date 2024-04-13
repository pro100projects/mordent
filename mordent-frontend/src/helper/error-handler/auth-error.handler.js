export const handleAuthError = (error) => {
  switch (error?.code) {
    case null: {
      return {};
    }
    case 'AUTH100002':
      return {
        code: error.code,
        field: getFieldsError(error.description),
        description: error.description
      };
    case 'AUTH100003':
      return {
        code: error.code,
        field: 'login',
        description: error.description
      };
    case 'AUTH100004':
      return {
        code: error.code,
        field: 'username',
        description: error.description
      };
    case 'AUTH100005':
      return {
        code: error.code,
        field: 'email',
        description: error.description
      };
    case 'AUTH100006':
      return {
        code: error.code,
        field: 'password',
        description: error.description
      };
    case 'AUTH100007':
      return {
        code: error.code,
        field: 'Security',
        type: 'notification',
        description: error.description
      };
    case 'AUTH100008':
    case 'AUTH100009':
      return {
        code: error.code,
        field: 'Security',
        type: 'notification',
        description: error.description
      };
    case 'AUTH100010':
      return {
        code: error.code,
        field: 'Security',
        type: 'notification',
        description: error.description
      };
    default:
      return {
        code: error.code,
        field: 'Mordent',
        type: 'notification',
        description: 'Technical problems. Please contact support'
      };
  }
};

const getFieldsError = (description) => {
  return new Map(Object.entries(JSON.parse(description)));
};
