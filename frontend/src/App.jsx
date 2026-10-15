import { useState, useEffect, useCallback } from 'react';
import { getClientes, createCliente, updateCliente, deleteCliente } from './api/clientes';
import { getMascotas, createMascota, updateMascota, deleteMascota } from './api/mascotas';

import ClienteTable from './components/ClienteTable';
import ClienteForm from './components/ClienteForm';
import MascotaTable from './components/MascotaTable';
import MascotaForm from './components/MascotaForm';
import Reportes from './components/Reportes';
import Sidebar from './components/Sidebar';
import AsistenteVoz from './components/AsistenteVoz';

import './App.css';

export default function App() {
  const [clientes, setClientes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);
  const [busqueda, setBusqueda] = useState('');
  const [modalAbierto, setModalAbierto] = useState(false);
  const [clienteEditando, setClienteEditando] = useState(null);

  const [mascotas, setMascotas] = useState([]);
  const [cargandoMascotas, setCargandoMascotas] = useState(true);
  const [errorMascotas, setErrorMascotas] = useState(null);
  const [busquedaMascotas, setBusquedaMascotas] = useState('');
  const [modalMascotaAbierto, setModalMascotaAbierto] = useState(false);
  const [mascotaEditando, setMascotaEditando] = useState(null);

  const [moduloActivo, setModuloActivo] = useState('inicio');

  const cargarClientes = useCallback(async () => {
    try {
      setCargando(true);
      setError(null);

      const data = await getClientes();

      setClientes(data);
    } catch {
      setError(
        'No se pudo conectar con el servidor. Verifique que el backend esté corriendo.'
      );
    } finally {
      setCargando(false);
    }
  }, []);

  const cargarMascotas = useCallback(async () => {
    try {
      setCargandoMascotas(true);
      setErrorMascotas(null);

      const data = await getMascotas();
      setMascotas(Array.isArray(data) ? data : []);
    } catch (error) {
      setErrorMascotas(
        error?.message || 'No se pudieron cargar las mascotas.'
      );
    } finally {
      setCargandoMascotas(false);
    }
  }, []);

  useEffect(() => {
    cargarClientes();
    cargarMascotas();
  }, [cargarClientes, cargarMascotas]);

  const clientesFiltrados = clientes.filter((c) =>
    `${c.nombre} ${c.apellido} ${c.email} ${c.telefono ?? ''}`
      .toLowerCase()
      .includes(busqueda.toLowerCase())
  );

  const clientesPorId = new Map(
    clientes.map((cliente) => [
      Number(cliente.id),
      `${cliente.nombre} ${cliente.apellido ?? ''}`.trim(),
    ])
  );

  const consultaMascotas = busquedaMascotas.trim().toLowerCase();
  const mascotasFiltradas = mascotas.filter((mascota) => {
    const nombreCliente = clientesPorId.get(Number(mascota.clienteId)) ?? '';
    const texto = [
      mascota.nombre,
      mascota.especie,
      mascota.raza,
      mascota.tipoPelo,
      nombreCliente,
    ]
      .filter(Boolean)
      .join(' ')
      .toLowerCase();

    return texto.includes(consultaMascotas);
  });

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

  function abrirCrearMascota() {
    setMascotaEditando(null);
    setModalMascotaAbierto(true);
  }

  function abrirEditarMascota(mascota) {
    setMascotaEditando(mascota);
    setModalMascotaAbierto(true);
  }

  function cerrarModalMascota() {
    setModalMascotaAbierto(false);
    setMascotaEditando(null);
  }

  async function guardarMascota(datos) {
    if (mascotaEditando) {
      await updateMascota(mascotaEditando.id, datos);
    } else {
      await createMascota(datos);
    }

    cerrarModalMascota();
    await cargarMascotas();
  }

  async function eliminarMascota(id) {
    if (!window.confirm('¿Seguro que desea eliminar esta mascota?')) return;

    try {
      await deleteMascota(id);
      await cargarMascotas();
    } catch (error) {
      setErrorMascotas(error?.message || 'No se pudo eliminar la mascota.');
    }
  }

  // ── CERRAR SESIÓN ──
  function cerrarSesion() {
    localStorage.removeItem('token');

    window.location.reload();
  }

  function renderContenido() {

    /* ── INICIO ── */
    if (moduloActivo === 'inicio') {
      return (
        <div className="dashboard">
          <div className="dashboard-header">
            <div>
              <h1>Inicio</h1>
              <p>Bienvenido al sistema de gestión de VeterinariaH 🐾</p>
            </div>
          </div>

          <div className="dashboard-cards">

            <div className="dashboard-card">
              <span className="dashboard-card-icon">👥</span>

              <div>
                <span className="dashboard-card-label">
                  Clientes registrados
                </span>

                <strong>{clientes.length}</strong>
              </div>
            </div>

            <div className="dashboard-card">
              <span className="dashboard-card-icon">🐾</span>

              <div>
                <span className="dashboard-card-label">
                  Mascotas
                </span>

                <strong>{mascotas.length}</strong>
              </div>
            </div>

            <div className="dashboard-card">
              <span className="dashboard-card-icon">🩺</span>

              <div>
                <span className="dashboard-card-label">
                  Servicios
                </span>

                <strong>—</strong>
              </div>
            </div>

          </div>
        </div>
      );
    }

    /* ── CLIENTES ── */
    if (moduloActivo === 'clientes') {
      return (
        <div className="app">

          <header className="app-header">
            <div className="header-brand">
              <span className="header-icon">👥</span>

              <h1>
                Clientes
                <span>Gestión de clientes</span>
              </h1>
            </div>

            <button
              className="btn-primary"
              onClick={abrirCrear}
            >
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
                  <h2 className="card-title">
                    Clientes
                  </h2>

                  {!cargando && !error && (
                    <span className="badge">
                      {clientesFiltrados.length}
                    </span>
                  )}
                </div>

                <div className="search-wrap">
                  <span className="search-icon">🔍</span>

                  <input
                    className="search-input"
                    type="search"
                    placeholder="Buscar por nombre, email…"
                    value={busqueda}
                    onChange={(e) =>
                      setBusqueda(e.target.value)
                    }
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

    /* ── MASCOTAS ── */
    if (moduloActivo === 'mascotas') {
      return (
        <div className="app">
          <header className="app-header">
            <div className="header-brand">
              <span className="header-icon">🐾</span>
              <h1>
                Mascotas
                <span>Gestión de mascotas y sus dueños</span>
              </h1>
            </div>

            <button className="btn-primary" onClick={abrirCrearMascota}>
              + Nueva mascota
            </button>
          </header>

          <main className="app-main">
            {errorMascotas && (
              <div className="error-banner" role="alert">
                <span aria-hidden="true">⚠️</span>
                {errorMascotas}
              </div>
            )}

            <div className="card">
              <div className="card-toolbar">
                <div className="card-toolbar-left">
                  <h2 className="card-title">Mascotas</h2>
                  {!cargandoMascotas && !errorMascotas && (
                    <span className="badge">{mascotasFiltradas.length}</span>
                  )}
                </div>

                <div className="search-wrap">
                  <span className="search-icon" aria-hidden="true">🔍</span>
                  <input
                    className="search-input"
                    type="search"
                    placeholder="Buscar mascota, especie o dueño…"
                    value={busquedaMascotas}
                    onChange={(event) => setBusquedaMascotas(event.target.value)}
                    aria-label="Buscar mascotas"
                  />
                </div>
              </div>

              {cargandoMascotas ? (
                <div className="estado-cargando" role="status">
                  <div className="spinner" aria-hidden="true" />
                  Cargando mascotas…
                </div>
              ) : (
                <MascotaTable
                  mascotas={mascotasFiltradas}
                  clientes={clientes}
                  busqueda={busquedaMascotas}
                  onEditar={abrirEditarMascota}
                  onEliminar={eliminarMascota}
                />
              )}
            </div>
          </main>

          {modalMascotaAbierto && (
            <MascotaForm
              mascota={mascotaEditando}
              clientes={clientes}
              onGuardar={guardarMascota}
              onCerrar={cerrarModalMascota}
            />
          )}
        </div>
      );
    }

    /* ── SERVICIOS ── */
    if (moduloActivo === 'servicios') {
      return (
        <div className="modulo-placeholder">
          <span>🩺</span>
          <h1>Servicios</h1>
          <p>Este módulo estará disponible próximamente.</p>
        </div>
      );
    }

    if (moduloActivo === 'reportes') {
      return <Reportes />;
    }
  }

  return (
    <div className="panel-layout">

      <Sidebar
        moduloActivo={moduloActivo}
        cambiarModulo={setModuloActivo}
        cerrarSesion={cerrarSesion}
      />

      <div className="panel-content">
        {renderContenido()}
      </div>

      <AsistenteVoz />
    </div>
  );
}
