import { useState, useEffect, useCallback } from 'react';
import { getClientes, createCliente, updateCliente, deleteCliente } from './api/clientes';
import ClienteTable from './components/ClienteTable';
import ClienteForm from './components/ClienteForm';
import './App.css';

export default function App() {
  const [clientes, setClientes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [busqueda, setBusqueda] = useState('');
  const [modalAbierto, setModalAbierto] = useState(false);
  const [clienteEditando, setClienteEditando] = useState(null);

  const cargarClientes = useCallback(async () => {
    try {
      setCargando(true);
      setError(null);
      const data = await getClientes();
      setClientes(data);
    } catch {
      setError('No se pudo conectar con el servidor. Verifique que el backend esté corriendo.');
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => { cargarClientes(); }, [cargarClientes]);

  const clientesFiltrados = clientes.filter((c) =>
    `${c.nombre} ${c.apellido} ${c.email} ${c.telefono ?? ''}`.toLowerCase()
      .includes(busqueda.toLowerCase())
  );

  function abrirCrear() {
    setClienteEditando(null);
    setModalAbierto(true);
  }

  function abrirEditar(cliente) {
    setClienteEditando(cliente);
    setModalAbierto(true);
  }

  function cerrarModal() {
    setModalAbierto(false);
    setClienteEditando(null);
  }

  async function guardar(datos) {
    if (clienteEditando) {
      await updateCliente(clienteEditando.id, datos);
    } else {
      await createCliente(datos);
    }
    cerrarModal();
    await cargarClientes();
  }

  async function eliminar(id) {
    if (!window.confirm('¿Seguro que desea eliminar este cliente?')) return;
    try {
      await deleteCliente(id);
      await cargarClientes();
    } catch {
      alert('No se pudo eliminar el cliente.');
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-brand">
          <span className="header-icon">🐾</span>
          <h1>
            VeterinariaH
            <span>Sistema de gestión veterinaria</span>
          </h1>
        </div>
        <button className="btn-primary" onClick={abrirCrear}>
          + Nuevo cliente
        </button>
      </header>

      <main className="app-main">
        {error && (
          <div className="error-banner">
            <span>⚠️</span>
            {error}
          </div>
        )}

        <div className="card">
          <div className="card-toolbar">
            <div className="card-toolbar-left">
              <h2 className="card-title">Clientes</h2>
              {!cargando && !error && (
                <span className="badge">{clientesFiltrados.length}</span>
              )}
            </div>
            <div className="search-wrap">
              <span className="search-icon">🔍</span>
              <input
                className="search-input"
                type="search"
                placeholder="Buscar por nombre, email…"
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
          </div>

          {cargando ? (
            <div className="estado-cargando">
              <div className="spinner" />
              Cargando clientes…
            </div>
          ) : (
            <ClienteTable
              clientes={clientesFiltrados}
              busqueda={busqueda}
              onEditar={abrirEditar}
              onEliminar={eliminar}
            />
          )}
        </div>
      </main>

      {modalAbierto && (
        <ClienteForm
          cliente={clienteEditando}
          onGuardar={guardar}
          onCerrar={cerrarModal}
        />
      )}
    </div>
  );
}
