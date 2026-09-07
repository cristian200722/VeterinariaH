import { StrictMode, useState } from 'react';
import { createRoot } from 'react-dom/client';

import './index.css';
import App from './App.jsx';
import Login from './components/Login.jsx';

function Principal() {
  const [logueado, setLogueado] = useState(
    !!localStorage.getItem('token')
  );

  function iniciarSesion() {
    setLogueado(true);
  }

  if (!logueado) {
    return <Login onLogin={iniciarSesion} />;
  }

  return <App />;
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Principal />
  </StrictMode>
);