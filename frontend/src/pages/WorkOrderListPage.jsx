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

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');

    workOrderApi
      .list(page, 20)
      .then((res) => {
        if (!cancelled) setData(res);
      })
      .catch((err) => {
        if (!cancelled) setError(err.message || 'Failed to load work orders.');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [page]);

  return (
    <div className="page-shell">
      <AppHeader />
      <main className="page-content">
        <div className="page-header-row">
          <h2>Work Orders</h2>
        </div>

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
