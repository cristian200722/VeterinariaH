import { useState } from 'react';

const VACIO = { nombre: '', apellido: '', email: '', telefono: '', direccion: '' };

export default function ClienteForm({ cliente, onGuardar, onCerrar }) {
  const [form, setForm] = useState(
    cliente
      ? {
          nombre: cliente.nombre,
          apellido: cliente.apellido,
          email: cliente.email,
          telefono: cliente.telefono ?? '',
          direccion: cliente.direccion ?? '',
        }
      : VACIO
  );
  const [errores, setErrores] = useState({});
  const [errorServidor, setErrorServidor] = useState(null);
  const [guardando, setGuardando] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value }));
    setErrores((prev) => ({ ...prev, [name]: undefined }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setGuardando(true);
    setErrorServidor(null);
    try {
      await onGuardar(form);
    } catch (err) {
      if (err?.error) {
        setErrorServidor(err.error);
      } else {
        setErrores(err ?? {});
      }
    } finally {
      setGuardando(false);
    }
  }

  return (
    <div className="modal-overlay" onClick={onCerrar}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>{cliente ? 'Editar cliente' : 'Nuevo cliente'}</h2>

        {errorServidor && <p className="error-msg">{errorServidor}</p>}

        <form onSubmit={handleSubmit} noValidate>
          <div className="campo">
            <label htmlFor="nombre">Nombre *</label>
            <input id="nombre" name="nombre" value={form.nombre} onChange={handleChange} />
            {errores.nombre && <span className="error-campo">{errores.nombre}</span>}
          </div>

          <div className="campo">
            <label htmlFor="apellido">Apellido *</label>
            <input id="apellido" name="apellido" value={form.apellido} onChange={handleChange} />
            {errores.apellido && <span className="error-campo">{errores.apellido}</span>}
          </div>

          <div className="campo">
            <label htmlFor="email">Email *</label>
            <input id="email" name="email" type="email" value={form.email} onChange={handleChange} />
            {errores.email && <span className="error-campo">{errores.email}</span>}
          </div>

          <div className="campo">
            <label htmlFor="telefono">Teléfono</label>
            <input id="telefono" name="telefono" value={form.telefono} onChange={handleChange} />
            {errores.telefono && <span className="error-campo">{errores.telefono}</span>}
          </div>

          <div className="campo">
            <label htmlFor="direccion">Dirección</label>
            <input id="direccion" name="direccion" value={form.direccion} onChange={handleChange} />
            {errores.direccion && <span className="error-campo">{errores.direccion}</span>}
          </div>

          <div className="modal-acciones">
            <button type="button" className="btn-cancelar" onClick={onCerrar}>Cancelar</button>
            <button type="submit" className="btn-primary" disabled={guardando}>
              {guardando ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
