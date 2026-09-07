const BASE = '/api/v1/clientes';

function getHeaders() {
  const token = localStorage.getItem('token');

  return {
    'Content-Type': 'application/json',
    ...(token && {
      Authorization: `Bearer ${token}`
    })
  };
}

async function parseResponse(res) {
  const text = await res.text();
  const body = text ? JSON.parse(text) : null;

  if (!res.ok) throw body;

  return body;
}

export async function getClientes() {
  const res = await fetch(BASE, {
    headers: getHeaders()
  });

  return parseResponse(res);
}

export async function createCliente(data) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(data),
  });

  return parseResponse(res);
}

export async function updateCliente(id, data) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify(data),
  });

  return parseResponse(res);
}

export async function deleteCliente(id) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'DELETE',
    headers: getHeaders()
  });

  return parseResponse(res);
}