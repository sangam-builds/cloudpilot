import axiosClient from './axiosClient';

export const agentApi = {
  getAgents: async () => {
    const res = await axiosClient.get('/agents');
    return res.data;
  },

  getAgentWorkload: async (id) => {
    const res = await axiosClient.get(`/agents/${id}/workload`);
    return res.data;
  },

  toggleAvailability: async (id, available) => {
    const res = await axiosClient.patch(`/agents/${id}/availability`, { available });
    return res.data;
  },

  getMetricsSummary: async () => {
    const res = await axiosClient.get('/metrics/summary');
    return res.data;
  },

  getBlastRadius: async (failedService) => {
    const res = await axiosClient.get('/metrics/blast-radius', { params: { failedService } });
    return res.data;
  },

  getAtRiskSla: async () => {
    const res = await axiosClient.get('/sla/at-risk');
    return res.data;
  },

  getAuditLogs: async (params = {}) => {
    const res = await axiosClient.get('/audit-logs', { params });
    return res.data;
  }
};
