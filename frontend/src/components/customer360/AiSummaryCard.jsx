import React from 'react';
import { Sparkles, BrainCircuit, ShieldCheck } from 'lucide-react';

export const AiSummaryCard = ({ summary, customerName }) => {
  return (
    <div style={{
      background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(168, 85, 247, 0.1) 100%)',
      border: '1px solid rgba(99, 102, 241, 0.35)',
      borderRadius: '14px',
      padding: '22px 26px',
      marginBottom: '24px',
      boxShadow: '0 4px 20px rgba(99, 102, 241, 0.15)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <BrainCircuit size={20} color="#818cf8" />
          <h3 style={{ fontSize: '1.05rem', fontWeight: 800, color: '#f8fafc' }}>
            Customer 360 AI Intelligence Summary
          </h3>
        </div>
        <span className="badge badge-purple" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
          <Sparkles size={11} />
          NLP Synthesized
        </span>
      </div>

      <p style={{
        fontSize: '0.95rem',
        color: '#e2e8f0',
        lineHeight: 1.6,
        fontWeight: 400
      }}>
        {summary || `Comprehensive AI profile is generated based on historical transactions, ticket resolution velocity, and customer sentiment signals.`}
      </p>
    </div>
  );
};
