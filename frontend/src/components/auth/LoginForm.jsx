import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { Zap, Lock, Mail, ArrowRight, Sparkles } from 'lucide-react';

export const LoginForm = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    const res = await login(email, password);
    if (res.success) {
      navigate('/dashboard');
    } else {
      setError(res.error);
      setLoading(false);
    }
  };

  const quickLogin = (presetEmail) => {
    setEmail(presetEmail);
  };

  return (
    <div className="glass-panel" style={{ width: '100%', maxWidth: '440px', padding: '36px 32px' }}>
      <div style={{ textAlign: 'center', marginBottom: '28px' }}>
        <div style={{
          width: '48px',
          height: '48px',
          margin: '0 auto 14px',
          borderRadius: '12px',
          background: 'linear-gradient(135deg, #6366f1 0%, #0ea5e9 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 0 20px rgba(99, 102, 241, 0.4)'
        }}>
          <Zap size={26} color="#fff" />
        </div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '6px' }}>Welcome to CloudPilot</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
          Sign in to access AI triage, Customer 360 &amp; SLA monitors
        </p>
      </div>

      {/* Demo Credentials Shortcuts */}
      <div style={{ background: 'rgba(255,255,255,0.03)', border: '1px solid var(--border-subtle)', borderRadius: '10px', padding: '12px 14px', marginBottom: '20px' }}>
        <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-muted)', display: 'block', marginBottom: '8px' }}>
          DEMO QUICK-SELECT ACCOUNTS:
        </span>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
          <button type="button" onClick={() => quickLogin('admin@cloudpilot.io')} className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '0.75rem' }}>
            👑 Admin
          </button>
          <button type="button" onClick={() => quickLogin('alex@cloudpilot.io')} className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '0.75rem' }}>
            👤 Support Agent
          </button>
          <button type="button" onClick={() => quickLogin('support@acmecorp.com')} className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '0.75rem' }}>
            🏢 Customer
          </button>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '16px' }}>
          <label className="form-label">Email Address</label>
          <div style={{ position: 'relative' }}>
            <input
              type="email"
              className="form-input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label className="form-label">Password</label>
          <div style={{ position: 'relative' }}>
            <input
              type="password"
              className="form-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
        </div>

        {error && (
          <div style={{ padding: '10px', background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', borderRadius: '8px', color: '#fca5a5', fontSize: '0.85rem', marginBottom: '16px' }}>
            {error}
          </div>
        )}

        <button
          type="submit"
          disabled={loading}
          className="btn btn-primary"
          style={{ width: '100%', padding: '12px', fontSize: '0.95rem' }}
        >
          {loading ? 'Authenticating...' : 'Sign In'}
          <ArrowRight size={16} />
        </button>
      </form>

      <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
        Don't have an account?{' '}
        <Link to="/register" style={{ color: '#818cf8', fontWeight: 600, textDecoration: 'none' }}>
          Register customer account
        </Link>
      </div>
    </div>
  );
};
