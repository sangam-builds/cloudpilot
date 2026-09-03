import React, { useState, useEffect } from 'react';
import { Navbar } from '../components/common/Navbar';
import { CreateTicketForm } from '../components/tickets/CreateTicketForm';
import { TicketList } from '../components/tickets/TicketList';
import { TicketDetail } from '../components/tickets/TicketDetail';
import { MetricsPanel } from '../components/admin/MetricsPanel';
import { ticketApi } from '../api/ticketApi';
import { useAuth } from '../context/AuthContext';
import { Sparkles, BarChart2, PlusCircle, Layers } from 'lucide-react';

export const DashboardPage = () => {
  const { user } = useAuth();
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);

  const fetchTickets = () => {
    setLoading(true);
    ticketApi.getTickets({
      status: statusFilter || undefined,
      priority: priorityFilter || undefined,
      size: 15
    })
      .then((res) => {
        const list = res.content || [];
        setTickets(list);
        if (list.length > 0 && !selectedTicket) {
          setSelectedTicket(list[0]);
        }
      })
      .catch((err) => console.log('Tickets load error:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchTickets();
  }, [statusFilter, priorityFilter]);

  const handleTicketCreated = (newTicket) => {
    setTickets((prev) => [newTicket, ...prev]);
    setSelectedTicket(newTicket);
  };

  return (
    <div>
      <Navbar />

      <main className="app-container">
        {/* Welcome Header */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '4px' }}>
              Command Center Overview
            </h1>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Real-time monitoring, auto-assignment queue &amp; SLA risk metrics
            </p>
          </div>

          <button
            onClick={() => setShowCreate(!showCreate)}
            className="btn btn-primary"
            style={{ padding: '10px 18px' }}
          >
            <PlusCircle size={16} />
            {showCreate ? 'Hide Create Form' : 'Create Live Ticket'}
          </button>
        </div>

        {/* Collapsible Create Ticket Live Simulation */}
        {showCreate && (
          <CreateTicketForm onTicketCreated={handleTicketCreated} />
        )}

        {/* KPI Panel */}
        <MetricsPanel />

        {/* Operational Split Pane: Left Feed, Right Detail */}
        <div style={{ marginTop: '28px' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '16px' }}>
            Active Support Queue &amp; Incident Resolution
          </h2>

          <div style={{ display: 'grid', gridTemplateColumns: '1.1fr 1.4fr', gap: '24px' }}>
            <div>
              <TicketList
                tickets={tickets}
                selectedTicket={selectedTicket}
                onSelectTicket={(t) => setSelectedTicket(t)}
                statusFilter={statusFilter}
                onStatusFilterChange={setStatusFilter}
                priorityFilter={priorityFilter}
                onPriorityFilterChange={setPriorityFilter}
                loading={loading}
              />
            </div>

            <div>
              <TicketDetail
                ticket={selectedTicket}
                onStatusUpdated={(updated) => {
                  setTickets((prev) => prev.map((t) => (t.id === updated.id ? updated : t)));
                  setSelectedTicket(updated);
                }}
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};
