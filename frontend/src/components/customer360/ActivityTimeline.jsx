import React from 'react';
import { ShoppingCart, Ticket, ArrowRight, CheckCircle, Clock } from 'lucide-react';

export const ActivityTimeline = ({ activities }) => {
  if (!activities || activities.length === 0) {
    return (
      <div style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '24px' }}>
        No recent activity recorded for this customer account.
      </div>
    );
  }

  const formatTimestamp = (ts) => {
    if (!ts) return 'Recent';
    const d = new Date(ts);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };

  return (
    <div style={{ position: 'relative', paddingLeft: '24px' }}>
      {/* Vertical Timeline Guide Line */}
      <div style={{
        position: 'absolute',
        top: '12px',
        bottom: '12px',
        left: '9px',
        width: '2px',
        background: 'rgba(255, 255, 255, 0.1)'
      }} />

      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {activities.map((item, idx) => {
          const isOrder = item.type === 'ORDER';
          return (
            <div key={idx} style={{ position: 'relative', display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
              {/* Timeline Bullet */}
              <div style={{
                position: 'absolute',
                left: '-24px',
                top: '4px',
                width: '20px',
                height: '20px',
                borderRadius: '50%',
                background: isOrder ? '#0ea5e9' : '#8b5cf6',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: isOrder ? '0 0 10px rgba(14, 165, 233, 0.5)' : '0 0 10px rgba(139, 92, 246, 0.5)'
              }}>
                {isOrder ? <ShoppingCart size={10} color="#fff" /> : <Ticket size={10} color="#fff" />}
              </div>

              {/* Activity Card */}
              <div style={{
                flex: 1,
                background: 'rgba(15, 23, 42, 0.6)',
                border: '1px solid var(--border-subtle)',
                borderRadius: '10px',
                padding: '12px 16px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                    <span style={{
                      fontSize: '0.7rem',
                      fontWeight: 800,
                      padding: '2px 6px',
                      borderRadius: '4px',
                      background: isOrder ? 'rgba(14,165,233,0.15)' : 'rgba(139,92,246,0.15)',
                      color: isOrder ? '#38bdf8' : '#c084fc',
                      textTransform: 'uppercase'
                    }}>
                      {item.type}
                    </span>
                    <strong style={{ fontSize: '0.9rem', color: 'var(--text-primary)' }}>
                      {item.title}
                    </strong>
                  </div>

                  {item.amount && (
                    <span style={{ fontSize: '0.85rem', color: '#34d399', fontWeight: 600 }}>
                      ${Number(item.amount).toLocaleString('en-US', { minimumFractionDigits: 2 })}
                    </span>
                  )}
                </div>

                <div style={{ textAlign: 'right' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>
                    {formatTimestamp(item.timestamp)}
                  </span>
                  <span className={`badge ${item.status === 'COMPLETED' || item.status === 'RESOLVED' ? 'badge-success' : 'badge-low'}`} style={{ fontSize: '0.65rem', marginTop: '4px' }}>
                    {item.status}
                  </span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
