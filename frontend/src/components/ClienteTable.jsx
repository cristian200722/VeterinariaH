export default function ClienteTable({ clientes, onEditar, onEliminar }) {
  if (clientes.length === 0) {
    return <p className="sin-datos">No hay clientes registrados.</p>;
  }

  return (
    <table className="tabla-clientes">
      <thead>
        <tr>
          <th>Nombre</th>
          <th>Apellido</th>
          <th>Email</th>
          <th>Teléfono</th>
          <th>Dirección</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {clientes.map((c) => (
          <tr key={c.id}>
            <td>{c.nombre}</td>
            <td>{c.apellido}</td>
            <td>{c.email}</td>
            <td>{c.telefono || '—'}</td>
            <td>{c.direccion || '—'}</td>
            <td className="acciones">
              <button className="btn-editar" onClick={() => onEditar(c)}>Editar</button>
              <button className="btn-eliminar" onClick={() => onEliminar(c.id)}>Eliminar</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
