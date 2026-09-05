import React from 'react';
import { Navbar } from '../components/common/Navbar';
import { AdminDashboard } from '../components/admin/AdminDashboard';

export const AdminPage = () => {
  return (
    <div>
      <Navbar />

      <main className="app-container">
        <div style={{ marginBottom: '24px' }}>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '4px' }}>
            System Administration &amp; SLA Management
          </h1>
          <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
            Real-time pipeline analytics, agent capacity tuning, blast radius simulator, and audit logging.
          </p>
        </div>

        <AdminDashboard />
      </main>
    </div>
  );
};
