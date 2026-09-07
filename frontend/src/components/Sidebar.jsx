import { useState } from 'react';

export default function Sidebar({
  moduloActivo,
  cambiarModulo,
  cerrarSesion,
}) {
  const [abierto, setAbierto] = useState(true);

  const modulos = [
    { id: 'inicio', icono: '🏠', nombre: 'Inicio' },
    { id: 'clientes', icono: '👥', nombre: 'Clientes' },
    { id: 'mascotas', icono: '🐾', nombre: 'Mascotas' },
    { id: 'servicios', icono: '🩺', nombre: 'Servicios' },
  ];

  return (
    <aside
      className={`sidebar ${
        abierto ? 'sidebar-abierto' : 'sidebar-cerrado'
      }`}
    >
      <div className="sidebar-header">
        <div className="sidebar-brand">
          <span className="sidebar-logo">🐾</span>

          {abierto && (
            <div className="sidebar-brand-text">
              <h2>VeterinariaH</h2>
              <span>Gestión veterinaria</span>
            </div>
          )}
        </div>

        <button
          className="sidebar-toggle"
          onClick={() => setAbierto(!abierto)}
          title={abierto ? 'Cerrar menú' : 'Abrir menú'}
        >
          ☰
        </button>
      </div>

      <nav className="sidebar-menu">
        {modulos.map((modulo) => (
          <button
            key={modulo.id}
            className={`sidebar-item ${
              moduloActivo === modulo.id ? 'sidebar-item-activo' : ''
            }`}
            onClick={() => cambiarModulo(modulo.id)}
            title={!abierto ? modulo.nombre : ''}
          >
            <span className="sidebar-item-icon">
              {modulo.icono}
            </span>

            {abierto && (
              <span className="sidebar-item-text">
                {modulo.nombre}
              </span>
            )}
          </button>
        ))}
      </nav>

      {/* Cerrar sesión */}
      <div className="sidebar-footer">
        <button
          className="sidebar-logout"
          onClick={cerrarSesion}
          title={!abierto ? 'Cerrar sesión' : ''}
        >
          <span className="sidebar-item-icon">🚪</span>

          {abierto && (
            <span className="sidebar-item-text">
              Cerrar sesión
            </span>
          )}
        </button>
      </div>
    </aside>
  );
}