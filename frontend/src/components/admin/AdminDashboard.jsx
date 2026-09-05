import React, { useState, useEffect } from 'react';
import { MetricsPanel } from './MetricsPanel';
import { AgentWorkloadTable } from './AgentWorkloadTable';
import { AuditLogViewer } from './AuditLogViewer';
import { agentApi } from '../../api/agentApi';
import { SlaTimer } from '../common/SlaTimer';
import { PriorityBadge } from '../common/PriorityBadge';
import { AlertTriangle, ShieldCheck, Zap } from 'lucide-react';
import { Link } from 'react-router-dom';

export const AdminDashboard = () => {
  const [atRiskTickets, setAtRiskTickets] = useState([]);
  const [loadingAtRisk, setLoadingAtRisk] = useState(true);

  const fetchAtRisk = () => {
    agentApi.getAtRiskSla()
      .then((res) => setAtRiskTickets(res || []))
      .catch((err) => console.log('At-risk load error:', err))
      .finally(() => setLoadingAtRisk(false));
  };

  useEffect(() => {
    fetchAtRisk();
    const interval = setInterval(fetchAtRisk, 10000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div>
      {/* SLA Alert Banner if Breaches or At-Risk Exist */}
      {atRiskTickets.length > 0 && (
        <div style={{
          background: 'linear-gradient(135deg, rgba(239, 68, 68, 0.15) 0%, rgba(245, 158, 11, 0.1) 100%)',
          border: '1px solid rgba(239, 68, 68, 0.4)',
          borderRadius: '12px',
          padding: '16px 20px',
          marginBottom: '24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '12px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <AlertTriangle size={20} color="#f87171" className="sla-at-risk" />
            <div>
              <strong style={{ color: '#fca5a5', fontSize: '0.95rem' }}>
                Active SLA Alert: {atRiskTickets.length} ticket(s) are At-Risk or Breached!
              </strong>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                Scheduled SLA monitor dynamically computing countdowns every 60 seconds.
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {atRiskTickets.slice(0, 3).map((t) => (
              <Link
                key={t.id}
                to={`/tickets?selected=${t.id}`}
                className="btn btn-secondary"
                style={{ padding: '6px 12px', fontSize: '0.75rem', borderColor: '#ef4444' }}
              >
                #{t.id} <PriorityBadge priority={t.priority} />
              </Link>
            ))}
          </div>
        </div>
      )}

      {/* Metrics, Graphs & DSA Traversal Simulator */}
      <MetricsPanel />

      {/* Agent Workload & Availability Matrix */}
      <AgentWorkloadTable />

      {/* Immutable Audit Log Trail */}
      <AuditLogViewer />
    </div>
  );
};
