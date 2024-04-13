import React, { useEffect, useState } from 'react';
import { Controller } from 'react-hook-form';
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
  TextField
} from '@mui/material';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import { passwordValidation } from '../../helper/validation/auth.validation';

export const TextFieldNotControlled = ({
  label = '',
  placeholder = '',
  type = 'text',
  rows = 1,
  multiline = false,
  focused = false,
  value,
  onChange = (_) => _,
  inputProps = {}
}) => {
  return (
    <TextField
      label={label}
      placeholder={placeholder}
      type={type}
      size="small"
      margin="normal"
      fullWidth={true}
      rows={rows}
      multiline={multiline}
      focused={focused}
      value={value ? value : ''}
      onChange={onChange}
      sx={{ marginTop: '5px' }}
      InputProps={inputProps}
    />
  );
};

export const CustomTextField = ({
  control,
  name,
  rules,
  label,
  type = 'text',
  rows = 1,
  multiline = false,
  focused = false,
  onChange = (_) => _,
  error,
  inputProps = {}
}) => {
  return (
    <Controller
      control={control}
      name={name}
      rules={rules}
      render={({ field }) => (
        <TextField
          label={label}
          type={type}
          size="small"
          margin="normal"
          fullWidth={true}
          rows={rows}
          multiline={multiline}
          focused={focused}
          value={field.value ? field.value : ''}
          onChange={(e) => {
            onChange();
            field.onChange(e);
          }}
          error={error?.message}
          helperText={error?.message}
          sx={{ marginTop: '5px' }}
          InputProps={inputProps}
        />
      )}
    />
  );
};

export const CustomPasswordTextField = ({ control, name, label, error }) => {
  const [showPassword, setShowPassword] = useState(false);

  const handleClickShowPassword = () => setShowPassword((show) => !show);

  const handleMouseDownPassword = (event) => {
    event.preventDefault();
  };

  return (
    <Controller
      control={control}
      name={name}
      rules={passwordValidation}
      render={({ field }) => (
        <FormControl
          fullWidth={true}
          variant="outlined"
          size="small"
          margin="normal"
          sx={{ marginTop: '5px' }}>
          <InputLabel htmlFor={name}>{label}</InputLabel>
          <OutlinedInput
            id={name}
            label={label}
            type={showPassword ? 'text' : 'password'}
            fullWidth={true}
            value={field.value ? field.value : ''}
            onChange={(e) => field.onChange(e)}
            error={error?.message}
            helperText={error?.message ? error?.message : 'Must be of 8 - 64 characters'}
            endAdornment={
              <InputAdornment position="end">
                <IconButton
                  aria-label="toggle password visibility"
                  onClick={handleClickShowPassword}
                  onMouseDown={handleMouseDownPassword}
                  edge="end">
                  {showPassword ? <Visibility /> : <VisibilityOff />}
                </IconButton>
              </InputAdornment>
            }
          />
          {error?.message && (
            <FormHelperText error id="accountId-error">
              {error?.message}
            </FormHelperText>
          )}
        </FormControl>
      )}
    />
  );
};

export const CustomFileTextField = ({
  label,
  focused = true,
  onChange = (_) => _,
  error,
  inputProps = {}
}) => {
  return (
    <TextField
      label={label}
      type="file"
      size="small"
      margin="normal"
      fullWidth={true}
      focused={focused}
      onChange={onChange}
      error={error?.message}
      helperText={error?.message}
      sx={{ marginTop: '5px' }}
      InputProps={inputProps}
    />
  );
};

export const CustomSelect = ({
  control,
  name,
  rules,
  label,
  values,
  focused = false,
  onChange = (_) => _,
  error,
  inputProps = {}
}) => {
  return (
    <Controller
      control={control}
      name={name}
      rules={rules}
      render={({ field }) => (
        <Select
          label={label}
          size="small"
          margin="normal"
          fullWidth={true}
          focused={focused}
          value={field.value ? field.value : ''}
          onChange={(e) => {
            onChange();
            field.onChange(e);
          }}
          error={error?.message}
          helperText={error?.message}
          sx={{ marginTop: '5px' }}
          InputProps={inputProps}>
          <MenuItem value={null}>Neither</MenuItem>
          {values.map((value) => (
            <MenuItem key={value.id} value={value.id}>
              {value.name}
            </MenuItem>
          ))}
        </Select>
      )}
    />
  );
};

export const CustomCheckBox = ({ control, name, label, defaultValue = false, setValue }) => {
  useEffect(() => {
    setValue(name, defaultValue);
  }, []);

  return (
    <FormControlLabel
      control={
        <Controller
          control={control}
          name={name}
          render={({ field }) => (
            <Checkbox
              value={field.value}
              checked={field.value}
              onChange={(e) => field.onChange(e.target.checked)}
            />
          )}
        />
      }
      label={label}
    />
  );
};
