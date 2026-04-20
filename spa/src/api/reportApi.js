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

    if (response.redirected) {
      handleAuthError();
      throw new Error('Session expired');
    }

    const contentType = response.headers.get('content-type') || '';

    if (!contentType.includes('application/json') && response.status !== 204) {
      handleAuthError();
      throw new Error('Session expired');
    }

    if (!response.ok) {
      if (response.status === 401) {
        handleAuthError();
        throw new Error('Session expired');
      }

      const text = await response.text();
      let message = response.statusText;
      try {
        const json = JSON.parse(text);
        message = json.message || message;
      } catch {
        message = text || response.statusText;
      }
      throw new Error(message);
    }

    if (response.status === 204) return null;
    return response.json();
  } catch (err) {
    if (err instanceof TypeError) {
      handleAuthError();
      throw new Error('Нет соединения с сервером');
    }
    throw err;
  }
}

export const buildingApi = {
  async getAll() {
    return apiFetch(`${API_BASE_URL}/buildings`);
  },

  async getById(id) {
    return apiFetch(`${API_BASE_URL}/buildings/${id}`);
  },

  async create(building) {
    return apiFetch(`${API_BASE_URL}/buildings`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(building),
    });
  },

  async update(id, building) {
    return apiFetch(`${API_BASE_URL}/buildings/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(building),
    });
  },

  async delete(id) {
    return apiFetch(`${API_BASE_URL}/buildings/${id}`, {
      method: 'DELETE',
    });
  }
};

export const reportApi = {
  async downloadBuildingReport(buildingId) {
    const response = await fetch(`${API_BASE_URL}/buildings-report/${buildingId}`, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      throw new Error('Failed to download report');
    }

    const blob = await response.blob();
    const contentDisposition = response.headers.get('Content-Disposition');
    const filename = contentDisposition
      ? contentDisposition.match(/filename="?([^";]+)"?/)[1]
      : 'security-report.txt';

    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
  }
};