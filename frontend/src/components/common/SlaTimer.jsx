import React, { useState, useEffect } from 'react';
import { Clock, AlertTriangle, AlertOctagon, CheckCheck } from 'lucide-react';

export const SlaTimer = ({ deadline, status, riskStatus }) => {
  const [timeLeft, setTimeLeft] = useState('');
  const [currentRisk, setCurrentRisk] = useState(riskStatus || 'ON_TRACK');

  useEffect(() => {
    if (!deadline) {
      setTimeLeft('N/A');
      return;
    }

    if (status === 'RESOLVED' || status === 'CLOSED') {
      setTimeLeft('Resolved');
      return;
    }

    const calculateTime = () => {
      const target = new Date(deadline).getTime();
      const now = new Date().getTime();
      const diff = target - now;

      if (diff <= 0) {
        const breachedMins = Math.abs(Math.floor(diff / 60000));
        setTimeLeft(`Breached by ${breachedMins}m`);
        setCurrentRisk('BREACHED');
        return;
      }

      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((diff % (1000 * 60)) / 1000);

      setTimeLeft(`${hours > 0 ? `${hours}h ` : ''}${minutes}m ${seconds}s`);

      if (hours === 0 && minutes < 30) {
        setCurrentRisk('AT_RISK');
      } else {
        setCurrentRisk('ON_TRACK');
      }
    };

    calculateTime();
    const interval = setInterval(calculateTime, 1000);
    return () => clearInterval(interval);
  }, [deadline, status]);

  if (status === 'RESOLVED' || status === 'CLOSED') {
    return (
      <span className="badge badge-success" style={{ gap: '4px' }}>
        <CheckCheck size={13} />
        SLA Met
      </span>
    );
  }

  if (currentRisk === 'BREACHED') {
    return (
      <span className="badge badge-high sla-breached" style={{ gap: '4px' }}>
        <AlertOctagon size={13} />
        {timeLeft}
      </span>
    );
  }

  if (currentRisk === 'AT_RISK') {
    return (
      <span className="badge badge-medium sla-at-risk" style={{ gap: '4px' }}>
        <AlertTriangle size={13} />
        {timeLeft}
      </span>
    );
  }

  return (
    <span className="badge badge-low mono" style={{ gap: '4px' }}>
      <Clock size={13} />
      {timeLeft}
    </span>
  );
};
