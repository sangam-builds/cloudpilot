import axiosClient from './axiosClient';

export const customerApi = {
  getCustomer: async (id) => {
    const res = await axiosClient.get(`/customers/${id}`);
    return res.data;
  },

  getCustomer360: async (id) => {
    const res = await axiosClient.get(`/customers/${id}/360`);
    return res.data;
  },

  listCustomers: async (params = {}) => {
    const res = await axiosClient.get('/customers', { params });
    return res.data;
  }
};
