import React from 'react';
import { AlertCircle, Clock, CheckCircle } from 'lucide-react';

export const PriorityBadge = ({ priority }) => {
  const p = (priority || 'MEDIUM').toUpperCase();

  if (p === 'HIGH') {
    return (
      <span className="badge badge-high">
        <AlertCircle size={12} />
        High
      </span>
    );
  }

  if (p === 'LOW') {
    return (
      <span className="badge badge-low">
        <CheckCircle size={12} />
        Low
      </span>
    );
  }

  return (
    <span className="badge badge-medium">
      <Clock size={12} />
      Medium
    </span>
  );
};
