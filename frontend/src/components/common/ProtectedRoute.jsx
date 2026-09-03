import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const ProtectedRoute = ({ children, requiredRole }) => {
  const { token, role, loading } = useAuth();

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
        <div style={{ color: 'var(--text-secondary)' }}>Loading CloudPilot...</div>
      </div>
    );
  }

  // Allow access in demo mode or check token
  if (!token && !localStorage.getItem('cloudpilot_role')) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && role !== requiredRole && role !== 'ADMIN') {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};
