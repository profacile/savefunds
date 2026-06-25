import axios from 'axios'
import { showToast } from './toast.service'

const apiBaseURL = import.meta.env.VITE_API_BASE_URL ?? (import.meta.env.DEV ? 'http://localhost:8080' : undefined)

if (!apiBaseURL) {
  throw new Error('VITE_API_BASE_URL doit etre defini pour les builds de production.')
}

const api = axios.create({
  baseURL: apiBaseURL,
  headers: { 'Content-Type': 'application/json' }
})

// Intercepteur request — injecte le token JWT
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Intercepteur response — gestion globale des erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const apiMessage = error.response?.data?.message

    if (!status) {
      showToast('error', 'Connexion impossible', 'Le serveur est inaccessible. Vérifiez votre connexion.')
      return Promise.reject(error)
    }

    switch (status) {
      case 400:
        showToast('warn', 'Données invalides', apiMessage ?? 'Veuillez vérifier les informations saisies.')
        break
      case 401:
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
        break
      case 403:
        showToast('error', 'Accès refusé', "Vous n'avez pas les droits pour effectuer cette action.")
        break
      case 404:
        showToast('warn', 'Ressource non trouvée', apiMessage ?? "La ressource demandée n'existe pas.", 4000)
        break
      case 500:
        showToast('error', 'Erreur serveur', 'Une erreur est survenue côté serveur. Réessayez plus tard.', 6000)
        break
      default:
        if (status >= 400) {
          showToast('error', 'Erreur', apiMessage ?? 'Une erreur inattendue est survenue.')
        }
    }

    return Promise.reject(error)
  }
)

export default api
