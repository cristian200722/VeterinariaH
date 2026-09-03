const COLORES_AVATAR = [
  '#0d9488', '#4f46e5', '#d97706', '#dc2626',
  '#7c3aed', '#0284c7', '#059669', '#db2777',
];

function Avatar({ nombre, apellido }) {
  const idx = ((nombre.charCodeAt(0) ?? 0) + (apellido.charCodeAt(0) ?? 0)) % COLORES_AVATAR.length;
  return (
    <div className="avatar" style={{ background: COLORES_AVATAR[idx] }}>
      {nombre[0]?.toUpperCase()}{apellido[0]?.toUpperCase()}
    </div>
  );
}

export default function ClienteTable({ clientes, busqueda, onEditar, onEliminar }) {
  if (clientes.length === 0) {
    return (
      <div className="estado-vacio">
        <span className="estado-vacio-icon">🐶</span>
        <h3>{busqueda ? 'Sin resultados' : 'No hay clientes aún'}</h3>
        <p>
          {busqueda
            ? `Ningún cliente coincide con "${busqueda}"`
            : 'Crea el primer cliente con el botón de arriba'}
        </p>
      </div>
    );
  }

  return (
    <table className="tabla-clientes">
      <thead>
        <tr>
          <th>Cliente</th>
          <th>Email</th>
          <th>Teléfono</th>
          <th>Dirección</th>
          <th style={{ textAlign: 'right' }}>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {clientes.map((c) => (
          <tr key={c.id}>
            <td>
              <div className="cliente-cell">
                <Avatar nombre={c.nombre} apellido={c.apellido} />
                <div>
                  <div className="cliente-nombre-completo">
                    {c.nombre} {c.apellido}
                  </div>
                  <div className="cliente-id-label">ID #{c.id}</div>
                </div>
              </div>
            </td>
            <td>
              <span className="email-text">{c.email}</span>
            </td>
            <td>
              {c.telefono
                ? <span>{c.telefono}</span>
                : <span className="text-muted">—</span>}
            </td>
            <td>
              {c.direccion
                ? <span>{c.direccion}</span>
                : <span className="text-muted">—</span>}
            </td>
            <td>
              <div className="acciones-cell">
                <button
                  className="btn-icon btn-icon-edit"
                  title="Editar"
                  onClick={() => onEditar(c)}
                >
                  ✏️
                </button>
                <button
                  className="btn-icon btn-icon-delete"
                  title="Eliminar"
                  onClick={() => onEliminar(c.id)}
                >
                  🗑️
                </button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
