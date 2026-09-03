import axiosClient from './axiosClient';

export const ticketApi = {
  getTickets: async (params = {}) => {
    const res = await axiosClient.get('/tickets', { params });
    return res.data;
  },

  getTicket: async (id) => {
    const res = await axiosClient.get(`/tickets/${id}`);
    return res.data;
  },

  createTicket: async (ticketData) => {
    const res = await axiosClient.post('/tickets', ticketData);
    return res.data;
  },

  updateStatus: async (id, status) => {
    const res = await axiosClient.patch(`/tickets/${id}/status`, { status });
    return res.data;
  },

  addComment: async (id, comment) => {
    const res = await axiosClient.post(`/tickets/${id}/comments`, { comment });
    return res.data;
  },

  getSuggestedReply: async (id) => {
    const res = await axiosClient.get(`/tickets/${id}/suggest-reply`);
    return res.data;
  }
};
