const API_BASE_URL = '/api';

const apiFetch = async (url, options = {}) => {
  const response = await fetch(url, {
    ...options,
    credentials: 'include',
  });

  if (response.redirected) {
    localStorage.removeItem('authUser');
    window.location.href = '/login';
    throw new Error('Session expired');
  }

  const contentType = response.headers.get('content-type') || '';
  if (!contentType.includes('application/json') && response.status !== 204) {
    localStorage.removeItem('authUser');
    window.location.href = '/login';
    throw new Error('Session expired');
  }

  if (!response.ok) {
    if (response.status === 401) {
      localStorage.removeItem('authUser');
      window.location.href = '/login';
      throw new Error('Session expired');
    }
    const text = await response.text();
    let message = response.statusText;
    try { message = JSON.parse(text).error || JSON.parse(text).message || message; } catch {}
    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
};

export const buildingApi = {
  async getAll() {
    return apiFetch(`${API_BASE_URL}/buildings`);
  },
  async getById(id) {
    return apiFetch(`${API_BASE_URL}/buildings/${id}`);
  },
  async update(id, data) {
    return apiFetch(`${API_BASE_URL}/buildings/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
  },
  async delete(id) {
    return apiFetch(`${API_BASE_URL}/buildings/${id}`, { method: 'DELETE' });
  },
};
