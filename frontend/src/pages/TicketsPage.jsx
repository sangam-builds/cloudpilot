import React, { useState, useEffect } from 'react';
import { Navbar } from '../components/common/Navbar';
import { CreateTicketForm } from '../components/tickets/CreateTicketForm';
import { TicketList } from '../components/tickets/TicketList';
import { TicketDetail } from '../components/tickets/TicketDetail';
import { ticketApi } from '../api/ticketApi';

export const TicketsPage = () => {
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [statusFilter, setStatusFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchTickets = () => {
    setLoading(true);
    ticketApi.getTickets({
      status: statusFilter || undefined,
      priority: priorityFilter || undefined,
      size: 20
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
        <div style={{ marginBottom: '24px' }}>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '4px' }}>
            Ticket Management &amp; AI Triage Pipeline
          </h1>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
            Filter by lifecycle status or priority, review LLM draft replies, and execute verified workflow transitions.
          </p>
        </div>

        {/* Create Ticket with presets */}
        <CreateTicketForm onTicketCreated={handleTicketCreated} />

        {/* Master-Detail Split Grid */}
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
      </main>
    </div>
  );
};
