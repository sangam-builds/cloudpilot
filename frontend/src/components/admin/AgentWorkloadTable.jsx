import React, { useState, useEffect } from 'react';
import { agentApi } from '../../api/agentApi';
import { UserCheck, UserX, Star, Tag, Activity } from 'lucide-react';

export const AgentWorkloadTable = () => {
  const [agents, setAgents] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchAgents = () => {
    agentApi.getAgents()
      .then((res) => setAgents(res))
      .catch((err) => console.log('Agents load error:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchAgents();
  }, []);

  const handleToggle = async (agentId, currentStatus) => {
    try {
      const updated = await agentApi.toggleAvailability(agentId, !currentStatus);
      setAgents((prev) => prev.map((a) => (a.id === agentId ? updated : a)));
    } catch (err) {
      console.error('Failed to toggle availability', err);
    }
  };

  if (loading) {
    return <div style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '24px' }}>Loading agents...</div>;
  }

  return (
    <div className="glass-panel" style={{ padding: '24px 28px', marginBottom: '28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '18px' }}>
        <div>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800 }}>Agent Availability &amp; Workload Matrix</h3>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
            Real-time workload weights factored directly into the $O(n \log n)$ agent assignment scorer
          </p>
        </div>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
          <thead>
            <tr style={{ borderBottom: '1px solid var(--border-subtle)', textAlign: 'left', color: 'var(--text-muted)' }}>
              <th style={{ padding: '10px 14px' }}>Agent</th>
              <th style={{ padding: '10px 14px' }}>Department</th>
              <th style={{ padding: '10px 14px' }}>Skill Tags</th>
              <th style={{ padding: '10px 14px' }}>Rating</th>
              <th style={{ padding: '10px 14px' }}>Active Workload</th>
              <th style={{ padding: '10px 14px' }}>Status / Toggle</th>
            </tr>
          </thead>
          <tbody>
            {agents.map((agent) => (
              <tr key={agent.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.04)' }}>
                <td style={{ padding: '12px 14px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <div style={{
                      width: '28px',
                      height: '28px',
                      borderRadius: '50%',
                      background: 'rgba(99, 102, 241, 0.2)',
                      color: '#818cf8',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontWeight: 700,
                      fontSize: '0.75rem'
                    }}>
                      {agent.name.charAt(0)}
                    </div>
                    <div>
                      <strong style={{ display: 'block', color: 'var(--text-primary)' }}>{agent.name}</strong>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{agent.email}</span>
                    </div>
                  </div>
                </td>

                <td style={{ padding: '12px 14px', color: '#93c5fd', fontWeight: 600 }}>
                  {agent.team?.name || 'General'}
                </td>

                <td style={{ padding: '12px 14px' }}>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                    {agent.skillTags?.split(',').map((tag, idx) => (
                      <span key={idx} className="badge badge-low" style={{ fontSize: '0.65rem' }}>
                        {tag.trim()}
                      </span>
                    ))}
                  </div>
                </td>

                <td style={{ padding: '12px 14px' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#fbbf24', fontWeight: 700 }}>
                    <Star size={13} fill="#fbbf24" />
                    {Number(agent.rating).toFixed(1)}
                  </span>
                </td>

                <td style={{ padding: '12px 14px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ fontWeight: 700, color: agent.currentWorkload > 5 ? '#f87171' : '#34d399' }}>
                      {agent.currentWorkload} tickets
                    </span>
                  </div>
                </td>

                <td style={{ padding: '12px 14px' }}>
                  <button
                    onClick={() => handleToggle(agent.id, agent.isAvailable)}
                    className={`btn ${agent.isAvailable ? 'btn-success' : 'btn-danger'}`}
                    style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                  >
                    {agent.isAvailable ? (
                      <>
                        <UserCheck size={13} />
                        Available
                      </>
                    ) : (
                      <>
                        <UserX size={13} />
                        Unavailable
                      </>
                    )}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
