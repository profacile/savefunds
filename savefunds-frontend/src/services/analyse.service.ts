import api from './api'

export const analyseService = {
  async create(entrepriseId: number, montantSouhaite: number) {
    const response = await api.post('/api/v1/analyses', { entrepriseId, montantSouhaite })
    return response.data
  },

  async analyser(id: number) {
    const response = await api.post(`/api/v1/analyses/${id}/analyser`)
    return response.data
  },

  async getResultat(id: number) {
    const response = await api.get(`/api/v1/analyses/${id}/resultat`)
    return response.data
  },

async getById(id: number) {
  const response = await api.get(`/api/v1/analyses/${id}`)
  return response.data
  },

  async getByEntreprise(entrepriseId: number) {
    const response = await api.get(`/api/v1/analyses/entreprise/${entrepriseId}`)
    return response.data
  }

}
