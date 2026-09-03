import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Navbar } from '../components/common/Navbar';
import { Customer360 } from '../components/customer360/Customer360';
import { Users } from 'lucide-react';

export const CustomerPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const currentId = id ? Number(id) : 1;

  const customers = [
    { id: 1, name: 'Acme Corporation' },
    { id: 2, name: 'TechCorp Global' },
    { id: 3, name: 'Omni Logistics' },
    { id: 4, name: 'Nova Financials' },
    { id: 5, name: 'Apex Cloud' },
    { id: 6, name: 'Vanguard Retailers' },
    { id: 7, name: 'Solstice Media' },
    { id: 8, name: 'Zenith Health' },
    { id: 9, name: 'HyperScale AI' },
    { id: 10, name: 'BluePeak Systems' },
  ];

  return (
    <div>
      <Navbar />

      <main className="app-container">
        {/* Customer Switcher Bar */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '4px' }}>
              Customer 360 Intelligence Console
            </h1>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Consolidated transactions, support tickets, and LLM synthesized relationship summary
            </p>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Users size={16} color="var(--text-muted)" />
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Switch Profile:</span>
            <select
              className="form-select"
              value={currentId}
              onChange={(e) => navigate(`/customer/${e.target.value}`)}
              style={{ width: 'auto', padding: '6px 14px', fontSize: '0.85rem' }}
            >
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} (#{c.id})
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Customer 360 Consolidated View */}
        <Customer360 customerId={currentId} />
      </main>
    </div>
  );
};
