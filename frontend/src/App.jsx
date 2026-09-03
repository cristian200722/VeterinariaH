import { useState, useEffect, useCallback } from 'react';
import { getClientes, createCliente, updateCliente, deleteCliente } from './api/clientes';
import ClienteTable from './components/ClienteTable';
import ClienteForm from './components/ClienteForm';
import './App.css';

export default function App() {
  const [clientes, setClientes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [clienteEditando, setClienteEditando] = useState(null);

  const cargarClientes = useCallback(async () => {
    try {
      setCargando(true);
      setError(null);
      const data = await getClientes();
      setClientes(data);
    } catch {
      setError('No se pudieron cargar los clientes. Verifique que el servidor esté corriendo.');
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    cargarClientes();
  }, [cargarClientes]);

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
        <h1>VeterinariaH — Clientes</h1>
        <button className="btn-primary" onClick={abrirCrear}>+ Nuevo cliente</button>
      </header>

      <main className="app-main">
        {error && <p className="error-msg">{error}</p>}

        {cargando ? (
          <p className="cargando">Cargando...</p>
        ) : (
          <ClienteTable
            clientes={clientes}
            onEditar={abrirEditar}
            onEliminar={eliminar}
          />
        )}
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
