import { useState } from 'react';
import { descargarReporte } from '../api/reportes';

const reportes = [
  {
    id: 'clientes',
    titulo: 'Reporte de clientes',
    descripcion: 'Listado actualizado de clientes, contacto y dirección.',
    icono: '👥',
    descargas: [
      { formato: 'PDF', ruta: 'clientes/pdf', archivo: 'reporte-clientes.pdf' },
      { formato: 'Excel', ruta: 'clientes/excel', archivo: 'reporte-clientes.xlsx' },
    ],
  },
  {
    id: 'mascotas',
    titulo: 'Reporte de mascotas',
    descripcion: 'Mascotas registradas, especie y datos de sus dueños.',
    icono: '🐾',
    descargas: [
      { formato: 'PDF', ruta: 'mascotas/pdf', archivo: 'reporte-mascotas.pdf' },
      { formato: 'Excel', ruta: 'mascotas/excel', archivo: 'reporte-mascotas.xlsx' },
    ],
  },
];

export default function Reportes() {
  const [descargando, setDescargando] = useState('');
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');

  async function generar(reporte, descarga) {
    const clave = `${reporte.id}-${descarga.formato}`;
    setDescargando(clave);
    setError('');
    setMensaje('');
    try {
      await descargarReporte(descarga.ruta, descarga.archivo);
      setMensaje(`${reporte.titulo} en ${descarga.formato} descargado.`);
    } catch (excepcion) {
      setError(excepcion?.message || 'No se pudo generar el reporte.');
    } finally {
      setDescargando('');
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-brand">
          <span className="header-icon" aria-hidden="true">📊</span>
          <h1>Reportes<span>Información generada desde la base de datos</span></h1>
        </div>
      </header>
      <main className="app-main">
        <section className="reportes-intro" aria-labelledby="reportes-titulo">
          <h2 id="reportes-titulo">Reportes disponibles</h2>
          <p>Genera una descarga actualizada en PDF o Excel.</p>
        </section>
        {error && <div className="error-banner" role="alert">⚠️ {error}</div>}
        {mensaje && <div className="reporte-mensaje" role="status">✓ {mensaje}</div>}
        <div className="reportes-grid">
          {reportes.map((reporte) => (
            <article className="reporte-card" key={reporte.id}>
              <div className="reporte-card-cabecera">
                <span className="reporte-icono" aria-hidden="true">{reporte.icono}</span>
                <div><h3>{reporte.titulo}</h3><p>{reporte.descripcion}</p></div>
              </div>
              <div className="reporte-acciones">
                {reporte.descargas.map((descarga) => {
                  const clave = `${reporte.id}-${descarga.formato}`;
                  return (
                    <button className="reporte-boton" key={descarga.formato} type="button"
                      disabled={Boolean(descargando)} onClick={() => generar(reporte, descarga)}>
                      {descargando === clave ? 'Generando…' : `Descargar ${descarga.formato}`}
                    </button>
                  );
                })}
              </div>
            </article>
          ))}
        </div>
      </main>
    </div>
  );
}
