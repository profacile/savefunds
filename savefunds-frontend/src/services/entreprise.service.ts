import api from './api'

export const entrepriseService = {
  async getByUserId(userId: number) {
    const response = await api.get(`/api/v1/entreprises/user/${userId}`)
    return response.data
  },

  async getById(id: number) {
    const response = await api.get(`/api/v1/entreprises/${id}`)
    return response.data
  },

  async create(data: any) {
    const response = await api.post('/api/v1/entreprises', data)
    return response.data
  },

  async update(id: number, data: any) {
    const response = await api.put(`/api/v1/entreprises/${id}`, data)
    return response.data
  }
}
