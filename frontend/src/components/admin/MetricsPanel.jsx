import React, { useState, useEffect } from 'react';
import { agentApi } from '../../api/agentApi';
import {
  TrendingUp,
  AlertTriangle,
  AlertOctagon,
  CheckCircle2,
  Users,
  Layers,
  Network,
  Activity
} from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Cell
} from 'recharts';

export const MetricsPanel = () => {
  const [metrics, setMetrics] = useState(null);
  const [blastService, setBlastService] = useState('Payment Service');
  const [blastResult, setBlastResult] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchMetrics = () => {
    agentApi.getMetricsSummary()
      .then((res) => setMetrics(res))
      .catch((err) => console.log('Metrics fetch error:', err))
      .finally(() => setLoading(false));
  };

  const handleRunBlastRadius = () => {
    agentApi.getBlastRadius(blastService)
      .then((res) => setBlastResult(res))
      .catch((err) => console.log('Blast radius error:', err));
  };

  useEffect(() => {
    fetchMetrics();
    handleRunBlastRadius();
    const interval = setInterval(fetchMetrics, 10000); // 10s auto-refresh
    return () => clearInterval(interval);
  }, []);

  const chartData = metrics?.statusBreakdown ? [
    { name: 'New', count: metrics.statusBreakdown.NEW || 0, color: '#a855f7' },
    { name: 'Assigned', count: metrics.statusBreakdown.ASSIGNED || 0, color: '#38bdf8' },
    { name: 'In Progress', count: metrics.statusBreakdown.IN_PROGRESS || 0, color: '#fbbf24' },
    { name: 'Resolved', count: metrics.statusBreakdown.RESOLVED || 0, color: '#34d399' },
    { name: 'Closed', count: metrics.statusBreakdown.CLOSED || 0, color: '#64748b' },
  ] : [];

  return (
    <div style={{ marginBottom: '28px' }}>
      {/* Top Stat Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', marginBottom: '20px' }}>
        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', color: '#818cf8', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700 }}>TOTAL TICKETS</span>
            <Activity size={16} />
          </div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800 }}>{metrics?.totalTickets || 0}</div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{metrics?.openTickets || 0} open / active</span>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', color: '#10b981', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700 }}>SLA ADHERENCE</span>
            <CheckCircle2 size={16} />
          </div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#34d399' }}>
            {metrics?.slaAdherenceRate || 98.5}%
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Target: &gt; 95%</span>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', color: '#f59e0b', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700 }}>AT-RISK TICKETS</span>
            <AlertTriangle size={16} />
          </div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#fbbf24' }}>
            {metrics?.atRiskCount || 0}
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>&lt; 20% SLA remaining</span>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', color: '#ef4444', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700 }}>SLA BREACHES</span>
            <AlertOctagon size={16} />
          </div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#f87171' }}>
            {metrics?.breachedCount || 0}
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Escalations triggered</span>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', color: '#38bdf8', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.75rem', fontWeight: 700 }}>QUEUE BACKLOG</span>
            <Layers size={16} />
          </div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#38bdf8' }}>
            {metrics?.queuedTicketsCount || 0}
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>In PriorityQueue</span>
        </div>
      </div>

      {/* Distribution Chart & Blast Radius Graph Tool */}
      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: '20px' }}>
        
        {/* Ticket Volume by Status Chart */}
        <div className="glass-panel" style={{ padding: '20px 24px' }}>
          <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '16px' }}>
            Ticket Pipeline Volume by Status
          </h3>
          <div style={{ width: '100%', height: 220 }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <XAxis dataKey="name" stroke="#64748b" fontSize={12} tickLine={false} />
                <YAxis stroke="#64748b" fontSize={12} tickLine={false} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#0f172a', borderColor: 'rgba(255,255,255,0.1)', borderRadius: '8px' }}
                  itemStyle={{ color: '#fff' }}
                />
                <Bar dataKey="count" radius={[6, 6, 0, 0]}>
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* DSA Service Dependency Graph Simulator */}
        <div className="glass-panel" style={{ padding: '20px 24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
            <Network size={18} color="#818cf8" />
            <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>
              Service Dependency Graph &amp; Blast Radius (BFS/DFS)
            </h3>
          </div>
          <p style={{ fontSize: '0.78rem', color: 'var(--text-secondary)', marginBottom: '14px' }}>
            Simulate cascading infrastructure dependencies to predict affected support queues.
          </p>

          <div style={{ display: 'flex', gap: '10px', marginBottom: '14px' }}>
            <select
              className="form-select"
              value={blastService}
              onChange={(e) => setBlastService(e.target.value)}
              style={{ fontSize: '0.85rem' }}
            >
              <option value="Payment Service">Payment Service</option>
              <option value="Database Primary">Database Primary</option>
              <option value="Auth Service">Auth Service</option>
              <option value="Order Service">Order Service</option>
              <option value="Shipping Logistics">Shipping Logistics</option>
            </select>
            <button
              type="button"
              onClick={handleRunBlastRadius}
              className="btn btn-primary"
              style={{ padding: '6px 14px', fontSize: '0.8rem', whiteSpace: 'nowrap' }}
            >
              Run Traversal
            </button>
          </div>

          {blastResult && (
            <div style={{ background: 'rgba(15,23,42,0.6)', padding: '12px 14px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '6px' }}>
                Impacted Downstream Services ({blastResult.impactedCount}):
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                {blastResult.affectedServicesBFS?.length > 0 ? (
                  blastResult.affectedServicesBFS.map((s, idx) => (
                    <span key={idx} className="badge badge-high" style={{ fontSize: '0.7rem' }}>
                      ⚠️ {s}
                    </span>
                  ))
                ) : (
                  <span style={{ fontSize: '0.8rem', color: '#34d399' }}>No downstream dependencies affected</span>
                )}
              </div>
            </div>
          )}
        </div>

      </div>
    </div>
  );
};
