import axiosClient from './axiosClient';

export const authApi = {
  login: async (credentials) => {
    const res = await axiosClient.post('/auth/login', credentials);
    return res.data;
  },

  register: async (userData) => {
    const res = await axiosClient.post('/auth/register', userData);
    return res.data;
  },

  refreshToken: async (refreshToken) => {
    const res = await axiosClient.post('/auth/refresh', { refreshToken });
    return res.data;
  }
};
