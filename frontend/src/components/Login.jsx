import { useState } from 'react';

const API_URL = import.meta.env.VITE_API_URL;

async function enviarSolicitud(ruta, datos) {
  const respuesta = await fetch(`${API_URL}/api/auth/${ruta}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos),
  });

  if (!respuesta.ok) {
    const cuerpo = await respuesta.json().catch(() => ({}));
    throw new Error(cuerpo.error || cuerpo.message || 'No se pudo completar la solicitud.');
  }
}

export default function Login({ onLogin }) {
  const [vista, setVista] = useState('login');
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [codigo, setCodigo] = useState('');
  const [nuevaContrasena, setNuevaContrasena] = useState('');
  const [confirmarContrasena, setConfirmarContrasena] = useState('');
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [cargando, setCargando] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setMensaje('');
    setCargando(true);

    try {
      const response = await fetch(`${API_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo, password }),
      });
      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(data.error || data.message || 'Correo o contraseña incorrectos.');
      }

      localStorage.setItem('token', data.token);
      onLogin();
    } catch (err) {
      setError(err.message || 'No fue posible iniciar sesión.');
    } finally {
      setCargando(false);
    }
  }

  async function solicitarCodigo(e) {
    e.preventDefault();
    setError('');
    setMensaje('');
    setCargando(true);

    try {
      await enviarSolicitud('recuperar-contrasena', { correo });
      setVista('codigo');
      setMensaje('Si ese correo corresponde a una cuenta, recibirás un código. Revisa también la carpeta de spam.');
    } catch (err) {
      setError(err.message);
    } finally {
      setCargando(false);
    }
  }

  async function restablecerContrasena(e) {
    e.preventDefault();
    setError('');
    setMensaje('');

    if (nuevaContrasena !== confirmarContrasena) {
      setError('Las contraseñas no coinciden.');
      return;
    }

    setCargando(true);
    try {
      await enviarSolicitud('restablecer-contrasena', {
        correo,
        codigo,
        nuevaContrasena,
      });
      setVista('login');
      setPassword('');
      setCodigo('');
      setNuevaContrasena('');
      setConfirmarContrasena('');
      setMensaje('Contraseña actualizada. Ya puedes iniciar sesión.');
    } catch (err) {
      setError(err.message);
    } finally {
      setCargando(false);
    }
  }

  function volverAlInicio() {
    setVista('login');
    setError('');
    setMensaje('');
  }

  return (
    <div className="login-page">
      <div className="login-box">
        <div className="login-brand">
          <div className="login-logo" aria-hidden="true">🐾</div>
          <div>
            <h1>VeterinariaH</h1>
            <span>Sistema de gestión veterinaria</span>
          </div>
        </div>

        <div className="login-divider" />

        {vista === 'login' && (
          <>
            <div className="login-title">
              <h2>Iniciar sesión</h2>
              <p>Ingresa tus datos para continuar</p>
            </div>
            {mensaje && <div className="login-success" role="status">{mensaje}</div>}
            {error && <div className="login-error" role="alert">⚠️ {error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="login-field">
                <label htmlFor="login-correo">Correo electrónico</label>
                <input
                  id="login-correo"
                  type="email"
                  autoComplete="username"
                  placeholder="correo@ejemplo.com"
                  value={correo}
                  onChange={(e) => setCorreo(e.target.value)}
                  required
                  autoFocus
                />
              </div>
              <div className="login-field">
                <label htmlFor="login-password">Contraseña</label>
                <input
                  id="login-password"
                  type="password"
                  autoComplete="current-password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="login-button" disabled={cargando}>
                {cargando ? 'Iniciando sesión…' : 'Iniciar sesión'}
              </button>
            </form>
            <button
              type="button"
              className="login-link"
              onClick={() => { setVista('solicitud'); setError(''); setMensaje(''); }}
            >
              ¿Olvidaste tu contraseña?
            </button>
          </>
        )}

        {vista === 'solicitud' && (
          <>
            <div className="login-title">
              <h2>Recuperar contraseña</h2>
              <p>Te enviaremos un código temporal al correo asociado a tu cuenta.</p>
            </div>
            {error && <div className="login-error" role="alert">⚠️ {error}</div>}
            <form onSubmit={solicitarCodigo}>
              <div className="login-field">
                <label htmlFor="recuperacion-correo">Correo electrónico</label>
                <input
                  id="recuperacion-correo"
                  type="email"
                  autoComplete="email"
                  placeholder="correo@ejemplo.com"
                  value={correo}
                  onChange={(e) => setCorreo(e.target.value)}
                  required
                  autoFocus
                  maxLength={150}
                />
              </div>
              <button type="submit" className="login-button" disabled={cargando}>
                {cargando ? 'Enviando…' : 'Enviar código'}
              </button>
            </form>
            <button type="button" className="login-link" onClick={volverAlInicio}>
              Volver a iniciar sesión
            </button>
          </>
        )}

        {vista === 'codigo' && (
          <>
            <div className="login-title">
              <h2>Crear contraseña nueva</h2>
              <p>Ingresa el código de 6 dígitos enviado a {correo}. Vence en 10 minutos.</p>
            </div>
            {mensaje && <div className="login-success" role="status">{mensaje}</div>}
            {error && <div className="login-error" role="alert">⚠️ {error}</div>}
            <form onSubmit={restablecerContrasena}>
              <div className="login-field">
                <label htmlFor="codigo-recuperacion">Código de recuperación</label>
                <input
                  id="codigo-recuperacion"
                  type="text"
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  pattern="[0-9]{6}"
                  maxLength={6}
                  placeholder="000000"
                  value={codigo}
                  onChange={(e) => setCodigo(e.target.value.replace(/\D/g, '').slice(0, 6))}
                  required
                  autoFocus
                />
              </div>
              <div className="login-field">
                <label htmlFor="nueva-contrasena">Nueva contraseña</label>
                <input
                  id="nueva-contrasena"
                  type="password"
                  autoComplete="new-password"
                  minLength={10}
                  maxLength={72}
                  value={nuevaContrasena}
                  onChange={(e) => setNuevaContrasena(e.target.value)}
                  required
                />
                <small>Debe tener entre 10 y 72 caracteres.</small>
              </div>
              <div className="login-field">
                <label htmlFor="confirmar-contrasena">Confirmar contraseña</label>
                <input
                  id="confirmar-contrasena"
                  type="password"
                  autoComplete="new-password"
                  minLength={10}
                  maxLength={72}
                  value={confirmarContrasena}
                  onChange={(e) => setConfirmarContrasena(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="login-button" disabled={cargando || codigo.length !== 6}>
                {cargando ? 'Actualizando…' : 'Cambiar contraseña'}
              </button>
            </form>
            <button type="button" className="login-link" onClick={volverAlInicio}>
              Volver a iniciar sesión
            </button>
          </>
        )}
      </div>
    </div>
  );
}