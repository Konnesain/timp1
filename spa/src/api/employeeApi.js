const API_BASE_URL = '/api';

function handleAuthError() {
  localStorage.removeItem('authUser');
  window.location.href = '/login';
}

async function apiFetch(url, options = {}) {
  try {
    const response = await fetch(url, {
      ...options,
      credentials: 'include',
    });

    // Spring Security redirects 401 to /login (HTML) — detect it
    if (response.redirected) {
      handleAuthError();
      throw new Error('Session expired');
    }

    const contentType = response.headers.get('content-type') || '';

    // Got HTML instead of JSON → session expired
    if (!contentType.includes('application/json') && response.status !== 204) {
      handleAuthError();
      throw new Error('Session expired');
    }

    if (!response.ok) {
      // 401 → session expired, redirect to login
      if (response.status === 401) {
        handleAuthError();
        throw new Error('Session expired');
      }

      // Read body once
      const text = await response.text();
      let message = response.statusText;
      try {
        message = JSON.parse(text).error || JSON.parse(text).message || message;
      } catch {
        message = text || response.statusText;
      }
      throw new Error(message);
    }

    if (response.status === 204) return null;
    return response.json();
  } catch (err) {
    // Network error (e.g., server down, connection reset)
    if (err instanceof TypeError) {
      handleAuthError();
      throw new Error('Нет соединения с сервером');
    }
    throw err;
  }
}

export const employeeApi = {
  async getAll() {
    return apiFetch(`${API_BASE_URL}/employees`);
  },

  async getById(id) {
    return apiFetch(`${API_BASE_URL}/employees/${id}`);
  },

  async create(employee) {
    return apiFetch(`${API_BASE_URL}/employees`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(employee),
    });
  },

  async update(id, employee) {
    return apiFetch(`${API_BASE_URL}/employees/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(employee),
    });
  },

  async delete(id) {
    return apiFetch(`${API_BASE_URL}/employees/${id}`, {
      method: 'DELETE',
    });
  },

  async getBuildings() {
    return apiFetch(`${API_BASE_URL}/buildings`);
  }
};

export const logApi = {
  async getEvents(page = 0, size = 50, type = null) {
    let url = `${API_BASE_URL}/logs?page=${page}&size=${size}`;
    if (type && type !== 'ALL') {
      url += `&type=${type}`;
    }
    return apiFetch(url);
  },

  async getEventTypes() {
    return apiFetch(`${API_BASE_URL}/logs/types`);
  }
};
