const BASE_URL = 'http://localhost:8080';

function getToken() {
  return localStorage.getItem('keystone_token');
}

function setToken(token) {
  if (token) {
    localStorage.setItem('keystone_token', token);
  } else {
    localStorage.removeItem('keystone_token');
  }
}

/**
 * Core request helper. Every endpoint call goes through this.
 * - Automatically attaches Authorization header if a token exists.
 * - Normalizes backend error responses (ApiError shape: { status, message, path, timestamp })
 *   into a single thrown Error with a readable message, so every page can just try/catch
 *   without knowing the response shape.
 */
async function request(path, { method = 'GET', body, auth = true } = {}) {
  const headers = { 'Content-Type': 'application/json' };

  if (auth) {
    const token = getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  // 204 No Content or empty bodies (e.g. logout) - nothing to parse
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = data?.message || `Request failed with status ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    error.data = data;
    throw error;
  }

  return data;
}

// ---------- Auth ----------
export const authApi = {
  login: (email, password) =>
    request('/api/v1/auth/login', { method: 'POST', body: { email, password }, auth: false }),
  logout: () => request('/api/v1/auth/logout', { method: 'POST' }),
};

// ---------- Work Orders ----------
export const workOrderApi = {
  list: (page = 0, size = 20) => request(`/api/work-orders?page=${page}&size=${size}`),
  getById: (id) => request(`/api/work-orders/${id}`),
  create: (payload) => request('/api/work-orders', { method: 'POST', body: payload }),
  assign: (id, technicianId) =>
    request(`/api/work-orders/${id}/assign`, { method: 'PATCH', body: { technicianId } }),
  transitionStatus: (id, toStatus, note) =>
    request(`/api/work-orders/${id}/status`, { method: 'PATCH', body: { toStatus, note } }),
};

// ---------- Customers ----------
export const customerApi = {
  list: (page = 0, size = 20) => request(`/api/customers?page=${page}&size=${size}`),
  getById: (id) => request(`/api/customers/${id}`),
  create: (payload) => request('/api/customers', { method: 'POST', body: payload }),
};

// ---------- Sites ----------
export const siteApi = {
  getById: (id) => request(`/api/sites/${id}`),
  byCustomer: (customerId, page = 0, size = 20) =>
    request(`/api/sites/customer/${customerId}?page=${page}&size=${size}`),
  create: (payload) => request('/api/sites', { method: 'POST', body: payload }),
};

// ---------- Time Logs ----------
export const timeLogApi = {
  byWorkOrder: (workOrderId) => request(`/api/time-logs/work-order/${workOrderId}`),
  log: (workOrderId, minutes) =>
    request('/api/time-logs', { method: 'POST', body: { workOrderId, minutes } }),
};

// ---------- Part Usage ----------
export const partUsageApi = {
  byWorkOrder: (workOrderId) => request(`/api/part-usage/work-order/${workOrderId}`),
  log: (workOrderId, partId, quantity) =>
    request('/api/part-usage', { method: 'POST', body: { workOrderId, partId, quantity } }),
};

// ---------- Parts ----------
export const partApi = {
  list: (page = 0, size = 20) => request(`/api/parts?page=${page}&size=${size}`),
};

// ---------- Dashboard ----------
export const reportApi = {
  dashboardSummary: () => request('/api/reports/dashboard-summary'),
};

export { getToken, setToken };
