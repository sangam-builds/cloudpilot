import React from 'react';
import { LoginForm } from '../components/auth/LoginForm';

export const LoginPage = () => {
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '24px'
    }}>
      <LoginForm />
    </div>
  );
};
