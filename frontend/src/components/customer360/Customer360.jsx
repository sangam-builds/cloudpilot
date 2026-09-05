import React, { useState, useEffect } from 'react';
import { customerApi } from '../../api/customerApi';
import { AiSummaryCard } from './AiSummaryCard';
import { ActivityTimeline } from './ActivityTimeline';
import { User, DollarSign, Package, Ticket, Mail, Phone, Calendar, Clock } from 'lucide-react';

export const Customer360 = ({ customerId }) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadData = () => {
    if (customerId) {
      setLoading(true);
      setError(null);
      customerApi.getCustomer360(customerId)
        .then((res) => setData(res))
        .catch((err) => setError(err.response?.data?.message || 'Failed to load Customer 360 data.'))
        .finally(() => setLoading(false));
    }
  };

  useEffect(() => {
    loadData();
  }, [customerId]);

  if (loading) {
    return (
      <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
        Aggregating Customer 360 intelligence &amp; running NLP summary...
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="glass-panel" style={{ padding: '40px', textAlign: 'center' }}>
        <p style={{ color: '#f87171', marginBottom: '16px', fontSize: '0.95rem' }}>
          {error || 'Customer profile not found.'}
        </p>
        <button
          onClick={loadData}
          className="btn btn-secondary"
          style={{ padding: '8px 16px', fontSize: '0.85rem' }}
        >
          Retry
        </button>
      </div>
    );
  }

  return (
    <div>
      {/* Customer Header */}
      <div className="glass-panel" style={{ padding: '24px 28px', marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{
              width: '54px',
              height: '54px',
              borderRadius: '14px',
              background: 'linear-gradient(135deg, #0ea5e9 0%, #6366f1 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '1.4rem',
              boxShadow: '0 4px 15px rgba(14, 165, 233, 0.4)'
            }}>
              {data.name.charAt(0).toUpperCase()}
            </div>
            <div>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '4px' }}>{data.name}</h2>
              <div style={{ display: 'flex', gap: '16px', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <Mail size={13} /> {data.email}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                  <Phone size={13} /> {data.phone || 'N/A'}
                </span>
              </div>
            </div>
          </div>

          <div style={{ textAlign: 'right' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Account Tier</span>
            <span className="badge badge-purple" style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
              Enterprise Tier
            </span>
          </div>
        </div>
      </div>

      {/* KPI Stats Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#10b981', marginBottom: '8px' }}>
            <DollarSign size={18} />
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)' }}>LIFETIME SPEND</span>
          </div>
          <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#34d399' }}>
            ${Number(data.totalSpend || 0).toLocaleString('en-US', { minimumFractionDigits: 2 })}
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#38bdf8', marginBottom: '8px' }}>
            <Package size={18} />
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)' }}>TOTAL ORDERS</span>
          </div>
          <div style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-primary)' }}>
            {data.totalOrders || 0}
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#fbbf24', marginBottom: '8px' }}>
            <Clock size={18} />
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)' }}>OPEN TICKETS</span>
          </div>
          <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#f59e0b' }}>
            {data.openTicketsCount || 0}
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '18px 20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#818cf8', marginBottom: '8px' }}>
            <Ticket size={18} />
            <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)' }}>RESOLVED TICKETS</span>
          </div>
          <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#818cf8' }}>
            {data.resolvedTicketsCount || 0}
          </div>
        </div>
      </div>

      {/* AI Intelligence Summary Card */}
      <AiSummaryCard summary={data.aiSummary} customerName={data.name} />

      {/* Unified Activity Timeline */}
      <div className="glass-panel" style={{ padding: '24px 28px' }}>
        <h3 style={{ fontSize: '1.1rem', fontWeight: 800, marginBottom: '20px' }}>
          Chronological Account Timeline
        </h3>
        <ActivityTimeline activities={data.recentActivity} />
      </div>
    </div>
  );
};
