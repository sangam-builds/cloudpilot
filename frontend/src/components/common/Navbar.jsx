import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  Zap,
  Ticket,
  UserCheck,
  ShieldAlert,
  BarChart3,
  LogOut,
  Layers,
  Sparkles,
  User
} from 'lucide-react';

export const Navbar = () => {
  const { user, role, logout, switchRole } = useAuth();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <header className="glass-panel" style={{ borderRadius: 0, borderTop: 0, borderLeft: 0, borderRight: 0, position: 'sticky', top: 0, zIndex: 100 }}>
      <div className="app-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 28px' }}>
        
        {/* Brand Logo */}
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '10px', textDecoration: 'none' }}>
          <div style={{
            background: 'linear-gradient(135deg, #6366f1 0%, #0ea5e9 100%)',
            padding: '8px',
            borderRadius: '10px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 0 15px rgba(99, 102, 241, 0.4)'
          }}>
            <Zap size={20} color="#ffffff" />
          </div>
          <div>
            <span style={{ fontSize: '1.25rem', fontWeight: 800, background: 'linear-gradient(90deg, #ffffff 0%, #94a3b8 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
              CloudPilot
            </span>
            <span style={{ marginLeft: '6px', fontSize: '0.65rem', padding: '2px 6px', background: 'rgba(99,102,241,0.2)', color: '#818cf8', borderRadius: '4px', fontWeight: 700, textTransform: 'uppercase' }}>
              AI Engine
            </span>
          </div>
        </Link>

        {/* Navigation Links */}
        <nav style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Link
            to="/dashboard"
            className="btn btn-secondary"
            style={{
              padding: '8px 14px',
              fontSize: '0.85rem',
              backgroundColor: isActive('/dashboard') ? 'rgba(99, 102, 241, 0.2)' : undefined,
              borderColor: isActive('/dashboard') ? '#6366f1' : undefined
            }}
          >
            <BarChart3 size={16} />
            Overview
          </Link>

          <Link
            to="/tickets"
            className="btn btn-secondary"
            style={{
              padding: '8px 14px',
              fontSize: '0.85rem',
              backgroundColor: isActive('/tickets') ? 'rgba(99, 102, 241, 0.2)' : undefined,
              borderColor: isActive('/tickets') ? '#6366f1' : undefined
            }}
          >
            <Ticket size={16} />
            Tickets &amp; Triage
          </Link>

          <Link
            to="/customer/1"
            className="btn btn-secondary"
            style={{
              padding: '8px 14px',
              fontSize: '0.85rem',
              backgroundColor: location.pathname.startsWith('/customer') ? 'rgba(99, 102, 241, 0.2)' : undefined,
              borderColor: location.pathname.startsWith('/customer') ? '#6366f1' : undefined
            }}
          >
            <UserCheck size={16} />
            Customer 360
          </Link>

          <Link
            to="/admin"
            className="btn btn-secondary"
            style={{
              padding: '8px 14px',
              fontSize: '0.85rem',
              backgroundColor: isActive('/admin') ? 'rgba(99, 102, 241, 0.2)' : undefined,
              borderColor: isActive('/admin') ? '#6366f1' : undefined
            }}
          >
            <ShieldAlert size={16} />
            Admin &amp; SLA Console
          </Link>
        </nav>

        {/* User Session & Demo Role Switcher */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
          {/* Quick Role Switcher (Crucial for Demoing) */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', background: 'rgba(255,255,255,0.04)', padding: '4px 8px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.06)' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Role:</span>
            <select
              value={role}
              onChange={(e) => switchRole(e.target.value)}
              style={{
                background: 'transparent',
                border: 'none',
                color: '#818cf8',
                fontSize: '0.8rem',
                fontWeight: 700,
                outline: 'none',
                cursor: 'pointer'
              }}
            >
              <option value="CUSTOMER" style={{ background: '#0f172a' }}>Customer</option>
              <option value="AGENT" style={{ background: '#0f172a' }}>Agent</option>
              <option value="ADMIN" style={{ background: '#0f172a' }}>Admin</option>
            </select>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{
              width: '32px',
              height: '32px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 700,
              fontSize: '0.85rem'
            }}>
              {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
            </div>
            <div style={{ display: 'flex', flexDirection: 'column' }}>
              <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                {user?.name || 'Demo User'}
              </span>
              <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                {user?.email || 'demo@cloudpilot.io'}
              </span>
            </div>
          </div>

          <button
            onClick={logout}
            className="btn btn-secondary"
            title="Log out"
            style={{ padding: '7px 10px', color: 'var(--text-muted)' }}
          >
            <LogOut size={16} />
          </button>
        </div>

      </div>
    </header>
  );
};
