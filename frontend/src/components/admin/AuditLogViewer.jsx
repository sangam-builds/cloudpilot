import React, { useState, useEffect } from 'react';
import { agentApi } from '../../api/agentApi';
import { ShieldCheck, ChevronLeft, ChevronRight, Filter } from 'lucide-react';

export const AuditLogViewer = () => {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);

  const fetchLogs = (p = 0) => {
    setLoading(true);
    agentApi.getAuditLogs({ page: p, size: 10 })
      .then((res) => {
        setLogs(res.content || []);
        setTotalPages(res.totalPages || 1);
        setPage(p);
      })
      .catch((err) => console.log('Audit log load error:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchLogs(0);
  }, []);

  const formatTimestamp = (ts) => {
    if (!ts) return '';
    return new Date(ts).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  return (
    <div className="glass-panel" style={{ padding: '24px 28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <ShieldCheck size={18} color="#10b981" />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800 }}>Immutable System &amp; Security Audit Trail</h3>
        </div>
        <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Append-only security log</span>
      </div>

      <div style={{ overflowX: 'auto', marginBottom: '16px' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.8rem' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-subtle)', textAlign: 'left', color: 'var(--text-muted)' }}>
              <th style={{ padding: '8px 12px' }}>Timestamp</th>
              <th style={{ padding: '8px 12px' }}>Actor</th>
              <th style={{ padding: '8px 12px' }}>Role</th>
              <th style={{ padding: '8px 12px' }}>Action</th>
              <th style={{ padding: '8px 12px' }}>Entity</th>
              <th style={{ padding: '8px 12px' }}>Payload</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.03)' }}>
                <td style={{ padding: '10px 12px', color: 'var(--text-muted)', whiteSpace: 'nowrap' }} className="mono">
                  {formatTimestamp(log.createdAt)}
                </td>
                <td style={{ padding: '10px 12px', fontWeight: 600, color: 'var(--text-primary)' }}>
                  {log.actorId}
                </td>
                <td style={{ padding: '10px 12px' }}>
                  <span className="badge badge-low" style={{ fontSize: '0.65rem' }}>{log.actorRole}</span>
                </td>
                <td style={{ padding: '10px 12px', color: '#818cf8', fontWeight: 700 }}>
                  {log.action}
                </td>
                <td style={{ padding: '10px 12px', color: 'var(--text-secondary)' }}>
                  {log.entityType} #{log.entityId}
                </td>
                <td style={{ padding: '10px 12px', maxWidth: '280px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }} className="mono">
                  <span style={{ color: '#94a3b8', fontSize: '0.75rem' }}>{log.details}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '8px' }}>
        <button
          disabled={page === 0}
          onClick={() => fetchLogs(page - 1)}
          className="btn btn-secondary"
          style={{ padding: '4px 8px', fontSize: '0.75rem' }}
        >
          <ChevronLeft size={14} />
        </button>
        <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          Page {page + 1} of {totalPages}
        </span>
        <button
          disabled={page >= totalPages - 1}
          onClick={() => fetchLogs(page + 1)}
          className="btn btn-secondary"
          style={{ padding: '4px 8px', fontSize: '0.75rem' }}
        >
          <ChevronRight size={14} />
        </button>
      </div>
    </div>
  );
};
