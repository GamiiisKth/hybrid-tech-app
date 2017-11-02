import React from 'react';
import ReactDOM from 'react-dom';
import App from 'src/containers/app.jsx';
import AppRouteComponent from '../containers/app-route.jsx';

ReactDOM.render(
  <App>
    <AppRouteComponent />
  </App>,
  document.getElementById('app')
);
