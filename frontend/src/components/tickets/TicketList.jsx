import React from 'react';
import { TicketCard } from './TicketCard';
import { Filter } from 'lucide-react';

export const TicketList = ({ tickets, selectedTicket, onSelectTicket, statusFilter, onStatusFilterChange, priorityFilter, onPriorityFilterChange, loading }) => {
  return (
    <div>
      {/* Filter Controls */}
      <div style={{ display: 'flex', gap: '10px', marginBottom: '16px' }}>
        <div style={{ flex: 1 }}>
          <select
            className="form-select"
            value={statusFilter}
            onChange={(e) => onStatusFilterChange(e.target.value)}
            style={{ fontSize: '0.85rem', padding: '8px 12px' }}
          >
            <option value="">All Statuses</option>
            <option value="NEW">New</option>
            <option value="ASSIGNED">Assigned</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CLOSED">Closed</option>
          </select>
        </div>

        <div style={{ flex: 1 }}>
          <select
            className="form-select"
            value={priorityFilter}
            onChange={(e) => onPriorityFilterChange(e.target.value)}
            style={{ fontSize: '0.85rem', padding: '8px 12px' }}
          >
            <option value="">All Priorities</option>
            <option value="HIGH">High Priority</option>
            <option value="MEDIUM">Medium Priority</option>
            <option value="LOW">Low Priority</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
          Loading tickets...
        </div>
      ) : tickets.length === 0 ? (
        <div className="glass-panel" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
          No tickets found matching the selected filters.
        </div>
      ) : (
        <div>
          {tickets.map((t) => (
            <TicketCard
              key={t.id}
              ticket={t}
              onClick={onSelectTicket}
              isSelected={selectedTicket?.id === t.id}
            />
          ))}
        </div>
      )}
    </div>
  );
};
