import api from './api'

export const authService = {
  async login(email: string, password: string) {
    const response = await api.post('/api/auth/login', { email, password })
    return response.data
  },

  async register(email: string, password: string, nom: string, prenom: string) {
    const response = await api.post('/api/auth/register', { email, password, nom, prenom })
    return response.data
  }
}
