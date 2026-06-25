import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView, meta: { public: true } },
    { path: '/register', component: RegisterView, meta: { public: true } },
    { path: '/dashboard', component: () => import('@/views/DashboardView.vue') },
    { path: '/entreprise', component: () => import('@/views/EntrepriseView.vue') },
    { path: '/analyse', component: () => import('@/views/AnalyseView.vue') },
    { path: '/resultat/:id', component: () => import('@/views/ResultatView.vue') },
    { path: '/profil', component: () => import('@/views/ProfilView.vue') },
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  // Route publique → laisser passer
  if (to.meta.public) {
    // Si déjà connecté et tente d'aller sur login/register → dashboard
    if (authStore.isAuthenticated()) return '/dashboard'
    return true
  }

  // Route protégée → vérifier le token
  if (!authStore.isAuthenticated()) return '/login'

  return true
})

export default router
