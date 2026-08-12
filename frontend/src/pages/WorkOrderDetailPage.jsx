import { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { workOrderApi, timeLogApi, partUsageApi } from '../api/client';
import { StatusBadge, PriorityBadge, ALLOWED_TRANSITIONS } from '../components/StatusBadge';
import AppHeader from '../components/AppHeader';

export default function WorkOrderDetailPage() {
  const { id } = useParams();
  const [wo, setWo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionError, setActionError] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  const [technicianId, setTechnicianId] = useState('');
  const [transitionNote, setTransitionNote] = useState('');

  const [minutes, setMinutes] = useState('');
  const [timeLogs, setTimeLogs] = useState([]);

  const [partId, setPartId] = useState('');
  const [quantity, setQuantity] = useState('');
  const [partUsages, setPartUsages] = useState([]);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [detail, logs, usages] = await Promise.all([
        workOrderApi.getById(id),
        timeLogApi.byWorkOrder(id).catch(() => []),
        partUsageApi.byWorkOrder(id).catch(() => []),
      ]);
      setWo(detail);
      setTimeLogs(Array.isArray(logs) ? logs : []);
      setPartUsages(Array.isArray(usages) ? usages : []);
    } catch (err) {
      setError(err.message || 'Failed to load work order.');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  async function handleAssign(e) {
    e.preventDefault();
    if (!technicianId.trim()) return;
    setActionError('');
    setActionLoading(true);
    try {
      await workOrderApi.assign(id, technicianId.trim());
      setTechnicianId('');
      await load();
    } catch (err) {
      setActionError(err.message || 'Failed to assign technician.');
    } finally {
      setActionLoading(false);
    }
  }

  async function handleTransition(toStatus) {
    setActionError('');
    setActionLoading(true);
    try {
      await workOrderApi.transitionStatus(id, toStatus, transitionNote || undefined);
      setTransitionNote('');
      await load();
    } catch (err) {
      setActionError(err.message || 'Status transition failed.');
    } finally {
      setActionLoading(false);
    }
  }

  async function handleLogTime(e) {
    e.preventDefault();
    const mins = parseInt(minutes, 10);
    if (!mins || mins <= 0) return;
    setActionError('');
    setActionLoading(true);
    try {
      await timeLogApi.log(id, mins);
      setMinutes('');
      await load();
    } catch (err) {
      setActionError(err.message || 'Failed to log time.');
    } finally {
      setActionLoading(false);
    }
  }

  async function handleLogPartUsage(e) {
    e.preventDefault();
    const qty = parseInt(quantity, 10);
    if (!partId.trim() || !qty || qty <= 0) return;
    setActionError('');
    setActionLoading(true);
    try {
      await partUsageApi.log(id, partId.trim(), qty);
      setPartId('');
      setQuantity('');
      await load();
    } catch (err) {
      setActionError(err.message || 'Failed to log part usage.');
    } finally {
      setActionLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="page-shell">
        <AppHeader />
        <main className="page-content"><p className="muted">Loading…</p></main>
      </div>
    );
  }

  if (error || !wo) {
    return (
      <div className="page-shell">
        <AppHeader />
        <main className="page-content">
          <div className="form-error">{error || 'Work order not found.'}</div>
          <Link to="/work-orders" className="btn btn-secondary" style={{ marginTop: 16 }}>
            Back to list
          </Link>
        </main>
      </div>
    );
  }

  const nextStatuses = ALLOWED_TRANSITIONS[wo.status] || [];

  return (
    <div className="page-shell">
      <AppHeader />
      <main className="page-content">
        <Link to="/work-orders" className="back-link">&larr; Back to work orders</Link>

        <div className="detail-header">
          <div>
            <h2>{wo.code} — {wo.title}</h2>
            <p className="muted">{wo.customerName} · {wo.siteName}</p>
          </div>
          <div className="detail-header-badges">
            <PriorityBadge priority={wo.priority} />
            <StatusBadge status={wo.status} />
          </div>
        </div>

        {actionError && <div className="form-error">{actionError}</div>}

        <div className="detail-grid">
          <section className="panel">
            <h3>Details</h3>
            <dl className="detail-list">
              <dt>Description</dt>
              <dd>{wo.description || <span className="muted">None provided</span>}</dd>
              <dt>Assigned technician</dt>
              <dd>{wo.assignedTechnicianName || <span className="muted">Unassigned</span>}</dd>
              <dt>SLA due</dt>
              <dd>{wo.slaDueAt ? new Date(wo.slaDueAt).toLocaleString() : '—'}</dd>
              <dt>Created</dt>
              <dd>{new Date(wo.createdAt).toLocaleString()}</dd>
              {wo.closedAt && (<><dt>Closed</dt><dd>{new Date(wo.closedAt).toLocaleString()}</dd></>)}
            </dl>
          </section>

          <section className="panel">
            <h3>Assign Technician</h3>
            {wo.status !== 'NEW' ? (
              <p className="muted">Assignment is only available while status is NEW.</p>
            ) : (
              <form onSubmit={handleAssign} className="inline-form">
                <input
                  type="text"
                  placeholder="Technician ID"
                  value={technicianId}
                  onChange={(e) => setTechnicianId(e.target.value)}
                />
                <button className="btn btn-primary btn-small" disabled={actionLoading}>
                  Assign
                </button>
              </form>
            )}
          </section>

          <section className="panel">
            <h3>Change Status</h3>
            {nextStatuses.length === 0 ? (
              <p className="muted">No further transitions available — this is a terminal state.</p>
            ) : (
              <>
                <input
                  type="text"
                  placeholder="Optional note"
                  value={transitionNote}
                  onChange={(e) => setTransitionNote(e.target.value)}
                  style={{ marginBottom: 8, width: '100%' }}
                />
                <div className="button-row">
                  {nextStatuses.map((status) => (
                    <button
                      key={status}
                      className="btn btn-secondary btn-small"
                      disabled={actionLoading}
                      onClick={() => handleTransition(status)}
                    >
                      Move to {status.replace('_', ' ')}
                    </button>
                  ))}
                </div>
              </>
            )}
          </section>

          <section className="panel">
            <h3>Status History</h3>
            {!wo.statusHistory || wo.statusHistory.length === 0 ? (
              <p className="muted">No transitions recorded yet.</p>
            ) : (
              <ul className="history-list">
                {wo.statusHistory.map((h) => (
                  <li key={h.id}>
                    <strong>{h.fromStatus || 'START'} → {h.toStatus}</strong>
                    <span className="muted"> by {h.changedByName} · {new Date(h.changedAt).toLocaleString()}</span>
                    {h.note && <div className="muted">"{h.note}"</div>}
                  </li>
                ))}
              </ul>
            )}
          </section>

          <section className="panel">
            <h3>Time Logs</h3>
            <form onSubmit={handleLogTime} className="inline-form">
              <input
                type="number"
                min="1"
                placeholder="Minutes"
                value={minutes}
                onChange={(e) => setMinutes(e.target.value)}
              />
              <button className="btn btn-primary btn-small" disabled={actionLoading}>Log time</button>
            </form>
            {timeLogs.length === 0 ? (
              <p className="muted" style={{ marginTop: 8 }}>No time logged yet.</p>
            ) : (
              <ul className="history-list">
                {timeLogs.map((t) => (
                  <li key={t.id}>
                    {t.minutes} min by {t.technicianName} · {new Date(t.loggedAt).toLocaleString()}
                  </li>
                ))}
              </ul>
            )}
          </section>

          <section className="panel">
            <h3>Parts Used</h3>
            <form onSubmit={handleLogPartUsage} className="inline-form">
              <input
                type="text"
                placeholder="Part ID"
                value={partId}
                onChange={(e) => setPartId(e.target.value)}
              />
              <input
                type="number"
                min="1"
                placeholder="Qty"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
                style={{ width: 70 }}
              />
              <button className="btn btn-primary btn-small" disabled={actionLoading}>Log usage</button>
            </form>
            {partUsages.length === 0 ? (
              <p className="muted" style={{ marginTop: 8 }}>No parts logged yet.</p>
            ) : (
              <ul className="history-list">
                {partUsages.map((p) => (
                  <li key={p.id}>
                    {p.quantity} × {p.partName} ({p.partSku}) · {new Date(p.loggedAt).toLocaleString()}
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>
      </main>
    </div>
  );
}
