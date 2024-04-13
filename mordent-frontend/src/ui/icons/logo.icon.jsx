import React from 'react';
import logo from './../../images/mordent.svg';

const LogoIcon = ({ style, width = 32, height = 32 }) => {
  return (
    <div style={style}>
      <img src={logo} alt={'Mordent'} width={width} height={height} />
    </div>
  );
};

export default LogoIcon;
