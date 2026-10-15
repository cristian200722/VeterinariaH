const API_URL = import.meta.env.VITE_API_URL?.replace(/\/+$/, '');
const BASE = API_URL ? `${API_URL}/api/v1/mascotas` : null;

function validarId(id, campo = 'identificador') {
  const valor = String(id ?? '');

  if (!/^[1-9]\d*$/.test(valor)) {
    throw new Error(`El ${campo} no es válido.`);
  }

  return encodeURIComponent(valor);
}

async function solicitar(ruta = '', opciones = {}) {
  if (!BASE) {
    throw new Error('No está configurada la URL del servidor.');
  }

  const token = localStorage.getItem('token');
  if (!token) {
    throw new Error('Tu sesión no está activa. Inicia sesión nuevamente.');
  }

  const headers = new Headers(opciones.headers ?? {});
  headers.set('Accept', 'application/json');
  headers.set('Authorization', `Bearer ${token}`);

  if (opciones.body !== undefined) {
    headers.set('Content-Type', 'application/json');
  }

  let respuesta;
  try {
    respuesta = await fetch(`${BASE}${ruta}`, { ...opciones, headers });
  } catch {
    throw new Error('No se pudo conectar con el servidor. Intenta de nuevo.');
  }

  const texto = await respuesta.text();
  let cuerpo = null;

  if (texto) {
    try {
      cuerpo = JSON.parse(texto);
    } catch {
      cuerpo = null;
    }
  }

  if (!respuesta.ok) {
    const error = new Error(
      cuerpo?.error || cuerpo?.message ||
      `La solicitud no pudo completarse (HTTP ${respuesta.status}).`
    );
    error.status = respuesta.status;
    error.data = cuerpo;
    throw error;
  }

  return cuerpo;
}

export async function getMascotas(clienteId) {
  const consulta = new URLSearchParams();

  if (clienteId !== undefined && clienteId !== null && clienteId !== '') {
    consulta.set('clienteId', validarId(clienteId, 'cliente'));
  }

  const query = consulta.toString();
  return solicitar(query ? `?${query}` : '');
}

export async function getMascota(id) {
  return solicitar(`/${validarId(id, 'mascota')}`);
}

export async function createMascota(data) {
  return solicitar('', {
    method: 'POST',
    body: JSON.stringify(data),
  });
}

export async function updateMascota(id, data) {
  return solicitar(`/${validarId(id, 'mascota')}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
}

export async function deleteMascota(id) {
  return solicitar(`/${validarId(id, 'mascota')}`, {
    method: 'DELETE',
  });
}