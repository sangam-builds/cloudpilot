import React, { useState } from 'react';
import { ticketApi } from '../../api/ticketApi';
import { Sparkles, Send, CheckCircle2, Bot, User, AlertCircle, ArrowRight } from 'lucide-react';
import { PriorityBadge } from '../common/PriorityBadge';

export const CreateTicketForm = ({ onTicketCreated }) => {
  const [customerId, setCustomerId] = useState(1);
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [createdResult, setCreatedResult] = useState(null);
  const [error, setError] = useState(null);

  // Quick preset templates for demoing live triage
  const presets = [
    {
      title: '💳 Double Billing Issue',
      subject: 'Double charge of $7,800 on monthly subscription',
      description: 'Our card was charged twice on invoice #9481. Need immediate refund for the duplicate transaction.'
    },
    {
      title: '🚨 Production API Outage',
      subject: 'Urgent: HTTP 504 Gateway Timeout across EU cluster',
      description: 'All microservice calls to our EU-West gateway are failing with 504 timeout errors. Total production blocker.'
    },
    {
      title: '📦 Customs Delivery Delay',
      subject: 'Shipment #DHL-884919 held at Frankfurt customs',
      description: 'Our express freight pallet is stuck at customs. Commercial invoice was attached.'
    },
    {
      title: '🔐 2FA Lockout',
      subject: 'Admin locked out after multiple failed 2FA attempts',
      description: 'Lost access to authenticator app. Need temporary recovery bypass token.'
    }
  ];

  const applyPreset = (preset) => {
    setSubject(preset.subject);
    setDescription(preset.description);
    setCreatedResult(null);
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!subject.trim() || !description.trim()) {
      setError('Please provide both subject and description.');
      return;
    }

    setLoading(true);
    setError(null);
    setCreatedResult(null);

    try {
      const res = await ticketApi.createTicket({
        customerId: Number(customerId),
        subject,
        description
      });
      setCreatedResult(res);
      if (onTicketCreated) {
        onTicketCreated(res);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit ticket. Please check backend connection.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="glass-panel" style={{ padding: '24px 28px', marginBottom: '28px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '18px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
            padding: '8px',
            borderRadius: '10px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <Sparkles size={18} color="#ffffff" />
          </div>
          <div>
            <h2 style={{ fontSize: '1.2rem', fontWeight: 800 }}>Create New Support Ticket</h2>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
              Real-time AI NLP triage, automatic SLA deadline math &amp; weighted agent routing
            </p>
          </div>
        </div>
      </div>

      {/* Preset Buttons for Fast Demo */}
      <div style={{ marginBottom: '20px' }}>
        <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', display: 'block', marginBottom: '8px' }}>
          QUICK SCENARIO PRESETS (FOR LIVE DEMO):
        </span>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
          {presets.map((p, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => applyPreset(p)}
              className="btn btn-secondary"
              style={{ padding: '6px 12px', fontSize: '0.75rem', borderRadius: '8px' }}
            >
              {p.title}
            </button>
          ))}
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '16px', marginBottom: '16px' }}>
          <div>
            <label className="form-label">Customer ID / Account</label>
            <select
              className="form-select"
              value={customerId}
              onChange={(e) => setCustomerId(e.target.value)}
            >
              <option value="1">Acme Corporation (ID: 1)</option>
              <option value="2">TechCorp Global (ID: 2)</option>
              <option value="3">Omni Logistics (ID: 3)</option>
              <option value="4">Nova Financials (ID: 4)</option>
              <option value="5">Apex Cloud (ID: 5)</option>
              <option value="6">Vanguard Retailers (ID: 6)</option>
            </select>
          </div>

          <div>
            <label className="form-label">Subject</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. Invoice discrepancy, 504 gateway timeout..."
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
            />
          </div>
        </div>

        <div style={{ marginBottom: '18px' }}>
          <label className="form-label">Description &amp; Incident Context</label>
          <textarea
            className="form-textarea"
            rows="3"
            placeholder="Describe the technical or account issue in detail..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>

        {error && (
          <div style={{ padding: '10px 14px', background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', borderRadius: '8px', color: '#fca5a5', fontSize: '0.85rem', marginBottom: '14px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <AlertCircle size={16} />
            {error}
          </div>
        )}

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '12px' }}>
          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
            style={{ padding: '10px 22px' }}
          >
            {loading ? (
              <>
                <Sparkles size={16} className="sla-at-risk" />
                AI Triaging &amp; Routing...
              </>
            ) : (
              <>
                <Send size={16} />
                Submit Ticket &amp; Run AI Pipeline
              </>
            )}
          </button>
        </div>
      </form>

      {/* Immediate Live Triage Result Reveal */}
      {createdResult && (
        <div style={{
          marginTop: '20px',
          padding: '18px 20px',
          background: 'rgba(99, 102, 241, 0.08)',
          border: '1px solid rgba(99, 102, 241, 0.3)',
          borderRadius: '12px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#10b981', fontWeight: 700, fontSize: '0.9rem', marginBottom: '12px' }}>
            <CheckCircle2 size={18} />
            Ticket #{createdResult.id} Created &amp; Auto-Triaged Successfully!
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '14px', fontSize: '0.85rem' }}>
            <div style={{ background: 'rgba(15,23,42,0.6)', padding: '10px 14px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>NLP Category</span>
              <strong style={{ color: '#818cf8' }}>{createdResult.category}</strong>
            </div>

            <div style={{ background: 'rgba(15,23,42,0.6)', padding: '10px 14px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>Computed Priority</span>
              <PriorityBadge priority={createdResult.priority} />
            </div>

            <div style={{ background: 'rgba(15,23,42,0.6)', padding: '10px 14px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>Detected Sentiment</span>
              <span className="badge badge-purple">{createdResult.sentiment}</span>
            </div>

            <div style={{ background: 'rgba(15,23,42,0.6)', padding: '10px 14px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
              <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem', display: 'block' }}>Assigned Support Agent</span>
              <strong style={{ color: '#38bdf8' }}>👤 {createdResult.assignedAgentName}</strong>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
