export const emailValidation = {
  required: 'Email cannot be blank',
  validate: (value) => {
    const regexp = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    if (!regexp.test(String(value).toLowerCase())) {
      return 'Email is not valid';
    }
    return true;
  }
};

export const passwordValidation = {
  required: 'Password cannot be blank',
  validate: (value) => {
    const regexp = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,64}$/;
    if (value.length < 8 || value.length > 64) {
      return 'Password must be of 8 - 64 characters';
    } else if (!regexp.test(value)) {
      return 'Password must be have uppercase letter, lowercase letter, number and special character';
    }
    return true;
  }
};
