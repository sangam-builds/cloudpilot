import React from 'react';
import { PriorityBadge } from '../common/PriorityBadge';
import { SlaTimer } from '../common/SlaTimer';
import { User, Tag, Sparkles, MessageSquare } from 'lucide-react';

export const TicketCard = ({ ticket, onClick, isSelected }) => {
  const getStatusBadge = (status) => {
    switch (status) {
      case 'NEW':
        return <span className="badge badge-purple">New</span>;
      case 'ASSIGNED':
        return <span className="badge badge-low">Assigned</span>;
      case 'IN_PROGRESS':
        return <span className="badge badge-medium">In Progress</span>;
      case 'RESOLVED':
        return <span className="badge badge-success">Resolved</span>;
      case 'CLOSED':
        return <span className="badge badge-low">Closed</span>;
      default:
        return <span className="badge badge-low">{status}</span>;
    }
  };

  const getSentimentColor = (sentiment) => {
    switch (sentiment) {
      case 'FRUSTRATED': return '#f87171';
      case 'NEGATIVE': return '#fb923c';
      case 'POSITIVE': return '#34d399';
      default: return '#94a3b8';
    }
  };

  return (
    <div
      onClick={() => onClick(ticket)}
      className="glass-panel-interactive"
      style={{
        padding: '18px 20px',
        cursor: 'pointer',
        marginBottom: '12px',
        borderLeft: isSelected ? '4px solid var(--accent-primary)' : '4px solid transparent',
        backgroundColor: isSelected ? 'rgba(99, 102, 241, 0.12)' : undefined
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--text-muted)' }}>
            #{ticket.id}
          </span>
          {getStatusBadge(ticket.status)}
          <PriorityBadge priority={ticket.priority} />
        </div>
        <SlaTimer deadline={ticket.slaDeadline} status={ticket.status} riskStatus={ticket.riskStatus} />
      </div>

      <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '8px', lineHeight: 1.3 }}>
        {ticket.subject}
      </h3>

      <p style={{
        fontSize: '0.85rem',
        color: 'var(--text-secondary)',
        marginBottom: '14px',
        display: '-webkit-box',
        WebkitLineClamp: 2,
        WebkitBoxOrient: 'vertical',
        overflow: 'hidden'
      }}>
        {ticket.description}
      </p>

      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--text-muted)', paddingTop: '10px', borderTop: '1px solid var(--border-subtle)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <User size={13} />
            {ticket.customerName || 'Customer'}
          </span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
            <Tag size={13} />
            {ticket.category || 'General'}
          </span>
        </div>

        {ticket.assignedAgentName && ticket.assignedAgentName !== 'Unassigned' ? (
          <span style={{ color: '#818cf8', fontWeight: 600, fontSize: '0.75rem' }}>
            👤 {ticket.assignedAgentName}
          </span>
        ) : (
          <span style={{ color: '#f59e0b', fontSize: '0.75rem', fontWeight: 600 }}>
            ⏳ Queued
          </span>
        )}
      </div>
    </div>
  );
};
