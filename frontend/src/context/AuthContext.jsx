import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('cloudpilot_token') || null);
  const [role, setRole] = useState(localStorage.getItem('cloudpilot_role') || 'CUSTOMER');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedUser = localStorage.getItem('cloudpilot_user');
    if (savedUser && token) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        console.error('Failed to parse cached user', e);
      }
    }
    setLoading(false);
  }, [token]);

  const login = async (email, password) => {
    try {
      const data = await authApi.login({ email, password });
      setToken(data.accessToken);
      setRole(data.role);
      const userProfile = { id: data.userId, email: data.email, name: data.name, role: data.role };
      setUser(userProfile);

      localStorage.setItem('cloudpilot_token', data.accessToken);
      localStorage.setItem('cloudpilot_role', data.role);
      localStorage.setItem('cloudpilot_user', JSON.stringify(userProfile));

      return { success: true, data };
    } catch (err) {
      const msg = err.response?.data?.message || 'Authentication failed';
      return { success: false, error: msg };
    }
  };

  const register = async (name, email, password) => {
    try {
      const data = await authApi.register({ name, email, password });
      setToken(data.accessToken);
      setRole(data.role);
      const userProfile = { id: data.userId, email: data.email, name: data.name, role: data.role };
      setUser(userProfile);

      localStorage.setItem('cloudpilot_token', data.accessToken);
      localStorage.setItem('cloudpilot_role', data.role);
      localStorage.setItem('cloudpilot_user', JSON.stringify(userProfile));

      return { success: true, data };
    } catch (err) {
      const msg = err.response?.data?.message || 'Registration failed';
      return { success: false, error: msg };
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    setRole('CUSTOMER');
    localStorage.removeItem('cloudpilot_token');
    localStorage.removeItem('cloudpilot_role');
    localStorage.removeItem('cloudpilot_user');
  };

  // Demo role switcher for testing all views
  const switchRole = (newRole) => {
    setRole(newRole);
    localStorage.setItem('cloudpilot_role', newRole);
    if (user) {
      const updated = { ...user, role: newRole };
      setUser(updated);
      localStorage.setItem('cloudpilot_user', JSON.stringify(updated));
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, role, loading, login, register, logout, switchRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
