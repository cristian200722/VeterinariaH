import { useState } from 'react';

export default function Login({ onLogin }) {
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();

    setError('');
    setCargando(true);

    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          correo,
          password,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.message || 'Correo o contraseña incorrectos');
      }

      localStorage.setItem('token', data.token);

      onLogin();
    } catch (err) {
      setError(err.message || 'No fue posible iniciar sesión');
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-box">

        <div className="login-brand">
          <div className="login-logo">🐾</div>

          <div>
            <h1>VeterinariaH</h1>
            <span>Sistema de gestión veterinaria</span>
          </div>
        </div>

        <div className="login-divider" />

        <div className="login-title">
          <h2>Iniciar sesión</h2>
          <p>Ingresa tus datos para continuar</p>
        </div>

        {error && (
          <div className="login-error">
            ⚠️ {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="login-field">
            <label>Correo electrónico</label>

            <input
              type="email"
              placeholder="correo@ejemplo.com"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className="login-field">
            <label>Contraseña</label>

            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="login-button"
            disabled={cargando}
          >
            {cargando ? 'Iniciando sesión...' : 'Iniciar sesión'}
          </button>
        </form>

      </div>
    </div>
  );
}