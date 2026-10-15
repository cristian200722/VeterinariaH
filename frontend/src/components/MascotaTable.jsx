function formatearFecha(fecha) {
  if (!fecha) return '—';

  const partes = fecha.split('-');
  if (partes.length !== 3) return fecha;

  const [anio, mes, dia] = partes;
  return `${dia}/${mes}/${anio}`;
}

export default function MascotaTable({
  mascotas = [],
  clientes = [],
  busqueda = '',
  onEditar,
  onEliminar,
}) {
  const nombresClientes = new Map(
    clientes.map((cliente) => [
      Number(cliente.id),
      `${cliente.nombre} ${cliente.apellido ?? ''}`.trim(),
    ])
  );

  if (mascotas.length === 0) {
    return (
      <div className="estado-vacio">
        <span className="estado-vacio-icon" aria-hidden="true">🐾</span>
        <h3>{busqueda ? 'Sin resultados' : 'No hay mascotas aún'}</h3>
        <p>
          {busqueda
            ? `Ninguna mascota coincide con "${busqueda}"`
            : 'Crea la primera mascota con el botón de arriba'}
        </p>
      </div>
    );
  }

  return (
    <div className="tabla-responsive">
      <table className="tabla-clientes tabla-mascotas">
        <thead>
          <tr>
            <th>Mascota</th>
            <th>Especie</th>
            <th>Raza</th>
            <th>Peso</th>
            <th>Fecha de nacimiento</th>
            <th>Dueño</th>
            <th style={{ textAlign: 'right' }}>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {mascotas.map((mascota) => {
            const nombreCliente =
              nombresClientes.get(Number(mascota.clienteId)) ??
              `Cliente #${mascota.clienteId}`;

            return (
              <tr key={mascota.id}>
                <td>
                  <div className="cliente-cell">
                    <span className="avatar mascota-avatar" aria-hidden="true">
                      🐾
                    </span>
                    <div>
                      <div className="cliente-nombre-completo">
                        {mascota.nombre}
                      </div>
                      <div className="cliente-id-label">ID #{mascota.id}</div>
                    </div>
                  </div>
                </td>
                <td>{mascota.especie}</td>
                <td>{mascota.raza || <span className="text-muted">—</span>}</td>
                <td>
                  {mascota.peso != null
                    ? `${mascota.peso} kg`
                    : <span className="text-muted">—</span>}
                </td>
                <td>{formatearFecha(mascota.fechaNacimiento)}</td>
                <td>{nombreCliente}</td>
                <td>
                  <div className="acciones-cell">
                    <button
                      type="button"
                      className="btn-icon btn-icon-edit"
                      title={`Editar ${mascota.nombre}`}
                      aria-label={`Editar mascota ${mascota.nombre}`}
                      onClick={() => onEditar(mascota)}
                    >
                      ✏️
                    </button>
                    <button
                      type="button"
                      className="btn-icon btn-icon-delete"
                      title={`Eliminar ${mascota.nombre}`}
                      aria-label={`Eliminar mascota ${mascota.nombre}`}
                      onClick={() => onEliminar(mascota.id)}
                    >
                      🗑️
                    </button>
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
