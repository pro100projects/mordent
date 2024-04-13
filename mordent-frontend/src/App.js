import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { Provider as ReduxStateProvider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';
import { createTheme, ThemeProvider } from '@mui/material';
import store, { persist } from './redux/store';
import { ToastContainer } from 'react-toastify';
import AppRoutes from './routes/app.routes';

import 'react-toastify/dist/ReactToastify.css';

const theme = createTheme({
  palette: {
    primary: {
      light: '#757ce8',
      main: '#0052fe',
      dark: '#002884',
      contrastText: '#fff'
    },
    secondary: {
      light: '#ff7961',
      main: '#f44336',
      dark: '#ba000d',
      contrastText: '#000'
    }
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
      '"Apple Color Emoji"',
      '"Segoe UI Emoji"',
      '"Segoe UI Symbol"'
    ].join(','),
    button: {
      textTransform: 'none'
    }
  }
});

const Providers = ({ children }) => {
  return (
    <ReduxStateProvider store={store}>
      <PersistGate loading="loading..." persistor={persist}>
        <ThemeProvider theme={theme}>
          <ToastContainer />
          <BrowserRouter>{children}</BrowserRouter>
        </ThemeProvider>
      </PersistGate>
    </ReduxStateProvider>
  );
};

const App = () => {
  return (
    <Providers>
      <AppRoutes />
    </Providers>
  );
};

export default App;
