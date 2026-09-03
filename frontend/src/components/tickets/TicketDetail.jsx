import React, { useState } from 'react';
import { PriorityBadge } from '../common/PriorityBadge';
import { SlaTimer } from '../common/SlaTimer';
import { TicketCommentThread } from './TicketCommentThread';
import { ticketApi } from '../../api/ticketApi';
import { useAuth } from '../../context/AuthContext';
import { User, Tag, Calendar, ShieldCheck, ArrowUpRight, CheckCircle2 } from 'lucide-react';
import { Link } from 'react-router-dom';

export const TicketDetail = ({ ticket, onStatusUpdated }) => {
  const { role } = useAuth();
  const [status, setStatus] = useState(ticket?.status || 'NEW');
  const [updating, setUpdating] = useState(false);
  const [statusMsg, setStatusMsg] = useState('');

  if (!ticket) {
    return (
      <div className="glass-panel" style={{ padding: '36px', textAlign: 'center', color: 'var(--text-muted)' }}>
        Select a ticket from the left list to view details, timeline, and AI Copilot responses.
      </div>
    );
  }

  const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    setStatus(newStatus);
    setUpdating(true);
    setStatusMsg('');

    try {
      const updated = await ticketApi.updateStatus(ticket.id, newStatus);
      setStatusMsg(`Status updated to ${newStatus}`);
      if (onStatusUpdated) onStatusUpdated(updated);
    } catch (err) {
      setStatusMsg(err.response?.data?.message || 'Failed to update status');
    } finally {
      setUpdating(false);
      setTimeout(() => setStatusMsg(''), 3000);
    }
  };

  return (
    <div className="glass-panel" style={{ padding: '28px 32px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '16px', marginBottom: '18px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 800, color: '#818cf8' }}>
              TICKET #{ticket.id}
            </span>
            <PriorityBadge priority={ticket.priority} />
            <span className="badge badge-purple">{ticket.category}</span>
          </div>
          <h2 style={{ fontSize: '1.35rem', fontWeight: 800, lineHeight: 1.3 }}>
            {ticket.subject}
          </h2>
        </div>

        {/* SLA Status Indicator */}
        <div style={{ textAlign: 'right' }}>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block', marginBottom: '4px' }}>SLA Target</span>
          <SlaTimer deadline={ticket.slaDeadline} status={ticket.status} riskStatus={ticket.riskStatus} />
        </div>
      </div>

      {/* Meta Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
        gap: '12px',
        padding: '14px 18px',
        background: 'rgba(15, 23, 42, 0.5)',
        borderRadius: '10px',
        border: '1px solid var(--border-subtle)',
        marginBottom: '20px'
      }}>
        <div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Customer</span>
          <Link to={`/customer/${ticket.customerId || 1}`} style={{ color: '#38bdf8', fontWeight: 600, fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '4px', textDecoration: 'none' }}>
            {ticket.customerName || 'Customer Account'}
            <ArrowUpRight size={12} />
          </Link>
        </div>

        <div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Assigned Agent</span>
          <strong style={{ fontSize: '0.85rem', color: 'var(--text-primary)' }}>
            👤 {ticket.assignedAgentName || 'Unassigned'}
          </strong>
        </div>

        <div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Department</span>
          <strong style={{ fontSize: '0.85rem', color: 'var(--text-primary)' }}>
            {ticket.assignedTeamName || ticket.category || 'General'}
          </strong>
        </div>

        <div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Workflow Status</span>
          <select
            value={status}
            onChange={handleStatusChange}
            disabled={updating}
            className="form-select"
            style={{ padding: '4px 8px', fontSize: '0.8rem', marginTop: '2px' }}
          >
            <option value="NEW">NEW</option>
            <option value="ASSIGNED">ASSIGNED</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="PENDING_CUSTOMER">PENDING_CUSTOMER</option>
            <option value="RESOLVED">RESOLVED</option>
            <option value="CLOSED">CLOSED</option>
          </select>
        </div>
      </div>

      {statusMsg && (
        <div style={{ padding: '8px 12px', background: 'rgba(16, 185, 129, 0.15)', color: '#34d399', borderRadius: '8px', fontSize: '0.8rem', marginBottom: '14px' }}>
          {statusMsg}
        </div>
      )}

      {/* Description Body */}
      <div style={{ marginBottom: '24px' }}>
        <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: 'var(--text-secondary)', marginBottom: '8px' }}>
          Issue Description
        </h4>
        <p style={{ fontSize: '0.95rem', color: 'var(--text-primary)', lineHeight: 1.6, background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-subtle)' }}>
          {ticket.description}
        </p>
      </div>

      {/* Comment Thread & AI Copilot Suggested Reply */}
      <TicketCommentThread ticketId={ticket.id} onCommentAdded={() => {}} />
    </div>
  );
};
