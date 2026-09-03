const BASE = '/api/v1/clientes';

async function parseResponse(res) {
  const text = await res.text();
  const body = text ? JSON.parse(text) : null;
  if (!res.ok) throw body;
  return body;
}

export async function getClientes() {
  const res = await fetch(BASE);
  return parseResponse(res);
}

export async function createCliente(data) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return parseResponse(res);
}

export async function updateCliente(id, data) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return parseResponse(res);
}

export async function deleteCliente(id) {
  const res = await fetch(`${BASE}/${id}`, { method: 'DELETE' });
  return parseResponse(res);
}
