import { useState } from 'react';

const VACIO = {
  clienteId: '',
  nombre: '',
  especie: '',
  raza: '',
  peso: '',
  tipoPelo: '',
  fechaNacimiento: '',
};

function fechaLocalHoy() {
  const hoy = new Date();
  const anio = hoy.getFullYear();
  const mes = String(hoy.getMonth() + 1).padStart(2, '0');
  const dia = String(hoy.getDate()).padStart(2, '0');
  return `${anio}-${mes}-${dia}`;
}

function obtenerValoresIniciales(mascota) {
  if (!mascota) return VACIO;

  return {
    clienteId: String(mascota.clienteId ?? ''),
    nombre: mascota.nombre ?? '',
    especie: mascota.especie ?? '',
    raza: mascota.raza ?? '',
    peso: mascota.peso != null ? String(mascota.peso) : '',
    tipoPelo: mascota.tipoPelo ?? '',
    fechaNacimiento: mascota.fechaNacimiento ?? '',
  };
}

export default function MascotaForm({
  mascota,
  clientes = [],
  onGuardar,
  onCerrar,
}) {
  const [form, setForm] = useState(() => obtenerValoresIniciales(mascota));
  const [errores, setErrores] = useState({});
  const [errorServidor, setErrorServidor] = useState('');
  const [guardando, setGuardando] = useState(false);

  const esEdicion = Boolean(mascota);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((actual) => ({ ...actual, [name]: value }));
    setErrores((actuales) => ({ ...actuales, [name]: undefined }));
    setErrorServidor('');
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setGuardando(true);
    setErrores({});
    setErrorServidor('');

    const datos = {
      clienteId: Number(form.clienteId),
      nombre: form.nombre.trim(),
      especie: form.especie.trim(),
      raza: form.raza.trim() || null,
      peso: form.peso === '' ? null : Number(form.peso),
      tipoPelo: form.tipoPelo.trim() || null,
      fechaNacimiento: form.fechaNacimiento || null,
    };

    try {
      await onGuardar(datos);
    } catch (error) {
      const datosError = error?.data;
      if (datosError && typeof datosError === 'object' && !datosError.error) {
        setErrores(datosError);
      } else {
        setErrorServidor(error?.message || 'No se pudo guardar la mascota.');
      }
    } finally {
      setGuardando(false);
    }
  }

  function intentarCerrar() {
    if (!guardando) onCerrar();
  }

  return (
    <div className="modal-overlay" onClick={intentarCerrar}>
      <section
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="mascota-form-titulo"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="modal-header">
          <h2 id="mascota-form-titulo">
            {esEdicion ? '✏️ Editar mascota' : '➕ Nueva mascota'}
          </h2>
          <button
            type="button"
            className="modal-close"
            onClick={intentarCerrar}
            aria-label="Cerrar formulario"
            disabled={guardando}
          >
            ✕
          </button>
        </div>

        <div className="modal-body">
          {errorServidor && (
            <p className="error-msg" role="alert">
              <span aria-hidden="true">⚠️</span> {errorServidor}
            </p>
          )}

          {clientes.length === 0 && (
            <p className="error-msg" role="alert">
              Primero registra un cliente para poder asociarlo a la mascota.
            </p>
          )}

          <form onSubmit={handleSubmit}>
            <div className="campo">
              <label htmlFor="mascota-cliente">Dueño *</label>
              <select
                id="mascota-cliente"
                name="clienteId"
                value={form.clienteId}
                onChange={handleChange}
                required
                disabled={guardando || clientes.length === 0}
                aria-invalid={Boolean(errores.clienteId)}
              >
                <option value="" disabled>Selecciona un cliente</option>
                {clientes.map((cliente) => (
                  <option key={cliente.id} value={cliente.id}>
                    {cliente.nombre} {cliente.apellido}
                  </option>
                ))}
              </select>
              {errores.clienteId && (
                <span className="error-campo">{errores.clienteId}</span>
              )}
            </div>

            <div className="form-fila">
              <div className="campo">
                <label htmlFor="mascota-nombre">Nombre *</label>
                <input
                  id="mascota-nombre"
                  name="nombre"
                  value={form.nombre}
                  onChange={handleChange}
                  maxLength={100}
                  required
                  autoFocus
                  disabled={guardando}
                  aria-invalid={Boolean(errores.nombre)}
                  placeholder="Ej. Luna"
                />
                {errores.nombre && (
                  <span className="error-campo">{errores.nombre}</span>
                )}
              </div>

              <div className="campo">
                <label htmlFor="mascota-especie">Especie *</label>
                <input
                  id="mascota-especie"
                  name="especie"
                  value={form.especie}
                  onChange={handleChange}
                  maxLength={100}
                  required
                  disabled={guardando}
                  aria-invalid={Boolean(errores.especie)}
                  placeholder="Ej. Perro"
                />
                {errores.especie && (
                  <span className="error-campo">{errores.especie}</span>
                )}
              </div>
            </div>

            <div className="form-fila">
              <div className="campo">
                <label htmlFor="mascota-raza">Raza</label>
                <input
                  id="mascota-raza"
                  name="raza"
                  value={form.raza}
                  onChange={handleChange}
                  maxLength={100}
                  disabled={guardando}
                  aria-invalid={Boolean(errores.raza)}
                  placeholder="Ej. Labrador"
                />
                {errores.raza && (
                  <span className="error-campo">{errores.raza}</span>
                )}
              </div>

              <div className="campo">
                <label htmlFor="mascota-peso">Peso (kg)</label>
                <input
                  id="mascota-peso"
                  name="peso"
                  type="number"
                  value={form.peso}
                  onChange={handleChange}
                  min="0.01"
                  step="0.01"
                  disabled={guardando}
                  aria-invalid={Boolean(errores.peso)}
                  placeholder="Ej. 8.50"
                />
                {errores.peso && (
                  <span className="error-campo">{errores.peso}</span>
                )}
              </div>
            </div>

            <div className="form-fila">
              <div className="campo">
                <label htmlFor="mascota-tipo-pelo">Tipo de pelo</label>
                <input
                  id="mascota-tipo-pelo"
                  name="tipoPelo"
                  value={form.tipoPelo}
                  onChange={handleChange}
                  maxLength={100}
                  disabled={guardando}
                  aria-invalid={Boolean(errores.tipoPelo)}
                  placeholder="Ej. Corto"
                />
                {errores.tipoPelo && (
                  <span className="error-campo">{errores.tipoPelo}</span>
                )}
              </div>

              <div className="campo">
                <label htmlFor="mascota-fecha">Fecha de nacimiento</label>
                <input
                  id="mascota-fecha"
                  name="fechaNacimiento"
                  type="date"
                  value={form.fechaNacimiento}
                  onChange={handleChange}
                  max={fechaLocalHoy()}
                  disabled={guardando}
                  aria-invalid={Boolean(errores.fechaNacimiento)}
                />
                {errores.fechaNacimiento && (
                  <span className="error-campo">{errores.fechaNacimiento}</span>
                )}
              </div>
            </div>

            <div className="modal-footer">
              <button
                type="button"
                className="btn-secondary"
                onClick={intentarCerrar}
                disabled={guardando}
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="btn-primary"
                disabled={guardando || clientes.length === 0}
              >
                {guardando
                  ? '⏳ Guardando…'
                  : esEdicion
                    ? 'Guardar cambios'
                    : 'Crear mascota'}
              </button>
            </div>
          </form>
        </div>
      </section>
    </div>
  );
}
