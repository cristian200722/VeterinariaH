const BASE = '/api/v1/clientes';

export async function getClientes() {
  const res = await fetch(BASE);
  if (!res.ok) throw new Error('Error al obtener clientes');
  return res.json();
}

export async function createCliente(data) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  const body = await res.json();
  if (!res.ok) throw body;
  return body;
}

export async function updateCliente(id, data) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  const body = await res.json();
  if (!res.ok) throw body;
  return body;
}

export async function deleteCliente(id) {
  const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw body;
  }
}
