const API_URL = import.meta.env.VITE_API_URL?.replace(/\/+$/, '');
const BASE = API_URL ? `${API_URL}/api/v1/reportes` : null;

export async function descargarReporte(ruta, nombreArchivo) {
  if (!BASE) throw new Error('No está configurada la URL del servidor.');
  const token = localStorage.getItem('token');
  if (!token) throw new Error('Tu sesión no está activa. Inicia sesión nuevamente.');

  let respuesta;
  try {
    respuesta = await fetch(`${BASE}/${ruta}`, {
      headers: {
        Authorization: `Bearer ${token}`,
        Accept: ruta.endsWith('/pdf') ? 'application/pdf'
          : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      },
    });
  } catch {
    throw new Error('No se pudo conectar con el servidor. Intenta de nuevo.');
  }

  if (!respuesta.ok) {
    const texto = await respuesta.text();
    let mensaje = `No se pudo generar el reporte (HTTP ${respuesta.status}).`;
    if (texto) {
      try {
        const cuerpo = JSON.parse(texto);
        mensaje = cuerpo?.error || cuerpo?.message || mensaje;
      } catch {
        // Conserva el mensaje HTTP cuando el servidor no devuelve JSON.
      }
    }
    throw new Error(mensaje);
  }

  const archivo = await respuesta.blob();
  const url = URL.createObjectURL(archivo);
  const enlace = document.createElement('a');
  enlace.href = url;
  enlace.download = nombreArchivo;
  document.body.appendChild(enlace);
  enlace.click();
  enlace.remove();
  window.setTimeout(() => URL.revokeObjectURL(url), 1000);
}