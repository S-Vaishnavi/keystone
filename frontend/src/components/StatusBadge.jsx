const STATUS_COLORS = {
  NEW: '#6b7280',
  ASSIGNED: '#2563eb',
  IN_PROGRESS: '#d97706',
  ON_HOLD: '#b91c1c',
  COMPLETED: '#059669',
  CLOSED: '#374151',
  CANCELLED: '#9ca3af',
};

export function StatusBadge({ status }) {
  const color = STATUS_COLORS[status] || '#6b7280';
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '3px 10px',
        borderRadius: '999px',
        fontSize: '12px',
        fontWeight: 600,
        letterSpacing: '0.02em',
        color: '#fff',
        backgroundColor: color,
      }}
    >
      {status.replace('_', ' ')}
    </span>
  );
}

const PRIORITY_COLORS = {
  CRITICAL: '#b91c1c',
  HIGH: '#d97706',
  MEDIUM: '#2563eb',
  LOW: '#6b7280',
};

export function PriorityBadge({ priority }) {
  const color = PRIORITY_COLORS[priority] || '#6b7280';
  return (
    <span style={{ color, fontWeight: 600, fontSize: '13px' }}>
      {priority}
    </span>
  );
}

// Mirrors WorkOrderLifecycleServiceImpl.ALLOWED_TRANSITIONS on the backend.
// Used only to decide which buttons to SHOW - the backend is still the source
// of truth and will reject anything invalid with a 409 regardless.
export const ALLOWED_TRANSITIONS = {
  NEW: ['ASSIGNED', 'CANCELLED'],
  ASSIGNED: ['IN_PROGRESS', 'CANCELLED'],
  IN_PROGRESS: ['ON_HOLD', 'COMPLETED'],
  ON_HOLD: ['IN_PROGRESS'],
  COMPLETED: ['CLOSED'],
  CLOSED: [],
  CANCELLED: [],
};
