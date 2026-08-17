import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { workOrderApi } from '../api/client';
import { StatusBadge, PriorityBadge } from '../components/StatusBadge';
import AppHeader from '../components/AppHeader';

export default function WorkOrderListPage() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [showForm, setShowForm] = useState(false);
  const [formError, setFormError] = useState('');
  const [formLoading, setFormLoading] = useState(false);
  const [form, setForm] = useState({
    customerId: '',
    siteId: '',
    title: '',
    description: '',
    priority: 'MEDIUM',
  });

  function load() {
    setLoading(true);
    setError('');
    workOrderApi
      .list(page, 20)
      .then((res) => setData(res))
      .catch((err) => setError(err.message || 'Failed to load work orders.'))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  function updateForm(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleCreate(e) {
    e.preventDefault();
    setFormError('');
    if (!form.customerId.trim() || !form.siteId.trim() || !form.title.trim()) {
      setFormError('Customer ID, Site ID, and Title are required.');
      return;
    }
    setFormLoading(true);
    try {
      await workOrderApi.create({
        customerId: form.customerId.trim(),
        siteId: form.siteId.trim(),
        title: form.title.trim(),
        description: form.description.trim(),
        priority: form.priority,
      });
      setForm({ customerId: '', siteId: '', title: '', description: '', priority: 'MEDIUM' });
      setShowForm(false);
      setPage(0);
      load();
    } catch (err) {
      setFormError(err.message || 'Failed to create work order.');
    } finally {
      setFormLoading(false);
    }
  }

  return (
    <div className="page-shell">
      <AppHeader />
      <main className="page-content">
        <div className="page-header-row">
          <h2>Work Orders</h2>
          <button className="btn btn-primary btn-small" onClick={() => setShowForm((s) => !s)}>
            {showForm ? 'Cancel' : '+ New Work Order'}
          </button>
        </div>

        {showForm && (
          <section className="panel" style={{ marginBottom: 20 }}>
            <h3>Create Work Order</h3>
            <p className="muted" style={{ marginTop: -8, marginBottom: 14 }}>
              Requires an existing Customer ID and Site ID (no picker yet — see README known limitations).
            </p>
            {formError && <div className="form-error">{formError}</div>}
            <form onSubmit={handleCreate} className="create-form-grid">
              <label className="field-label">
                Customer ID
                <input
                  type="text"
                  value={form.customerId}
                  onChange={(e) => updateForm('customerId', e.target.value)}
                  placeholder="uuid"
                />
              </label>
              <label className="field-label">
                Site ID
                <input
                  type="text"
                  value={form.siteId}
                  onChange={(e) => updateForm('siteId', e.target.value)}
                  placeholder="uuid"
                />
              </label>
              <label className="field-label" style={{ gridColumn: '1 / -1' }}>
                Title
                <input
                  type="text"
                  value={form.title}
                  onChange={(e) => updateForm('title', e.target.value)}
                  placeholder="HVAC unit not cooling"
                />
              </label>
              <label className="field-label" style={{ gridColumn: '1 / -1' }}>
                Description
                <input
                  type="text"
                  value={form.description}
                  onChange={(e) => updateForm('description', e.target.value)}
                  placeholder="Optional details"
                />
              </label>
              <label className="field-label">
                Priority
                <select
                  value={form.priority}
                  onChange={(e) => updateForm('priority', e.target.value)}
                >
                  <option value="CRITICAL">CRITICAL</option>
                  <option value="HIGH">HIGH</option>
                  <option value="MEDIUM">MEDIUM</option>
                  <option value="LOW">LOW</option>
                </select>
              </label>
              <div style={{ alignSelf: 'end' }}>
                <button className="btn btn-primary" disabled={formLoading}>
                  {formLoading ? 'Creating…' : 'Create'}
                </button>
              </div>
            </form>
          </section>
        )}

        {loading && <p className="muted">Loading…</p>}
        {error && <div className="form-error">{error}</div>}

        {!loading && !error && data && (
          <>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Title</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Customer</th>
                  <th>Site</th>
                  <th>Technician</th>
                  <th>SLA Due</th>
                </tr>
              </thead>
              <tbody>
                {data.content.length === 0 && (
                  <tr>
                    <td colSpan={8} className="muted" style={{ textAlign: 'center', padding: '24px' }}>
                      No work orders found.
                    </td>
                  </tr>
                )}
                {data.content.map((wo) => (
                  <tr key={wo.id}>
                    <td>
                      <Link to={`/work-orders/${wo.id}`} className="row-link">
                        {wo.code}
                      </Link>
                    </td>
                    <td>{wo.title}</td>
                    <td><PriorityBadge priority={wo.priority} /></td>
                    <td><StatusBadge status={wo.status} /></td>
                    <td>{wo.customerName}</td>
                    <td>{wo.siteName}</td>
                    <td>{wo.assignedTechnicianName || <span className="muted">Unassigned</span>}</td>
                    <td>{wo.slaDueAt ? new Date(wo.slaDueAt).toLocaleString() : '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="pagination">
              <button
                className="btn btn-secondary"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                Previous
              </button>
              <span className="muted">
                Page {data.page + 1} of {Math.max(data.totalPages, 1)}
              </span>
              <button
                className="btn btn-secondary"
                disabled={page + 1 >= data.totalPages}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </button>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
