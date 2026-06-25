<template>
  <div class="app-layout">

    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <img src="@/assets/logo-savefunds.jpeg" alt="SaveFunds" class="sidebar-logo" />
        <span class="sidebar-title">SaveFunds</span>
      </div>

      <nav class="sidebar-nav">
        <RouterLink to="/dashboard" class="nav-item">
          <span class="nav-icon">📊</span>
          <span>Dashboard</span>
        </RouterLink>
        <RouterLink to="/entreprise" class="nav-item">
          <span class="nav-icon">🏢</span>
          <span>Mon Entreprise</span>
        </RouterLink>
        <RouterLink to="/analyse" class="nav-item">
          <span class="nav-icon">🔍</span>
          <span>Nouvelle Analyse</span>
        </RouterLink>
        <RouterLink to="/profil" class="nav-item">
          <span class="nav-icon">👤</span>
          <span>Mon Profil</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="user-avatar">{{ userInitials }}</div>
          <div class="user-details">
            <span class="user-name">{{ userName }}</span>
            <span class="user-role">Dirigeant</span>
          </div>
        </div>
        <button class="btn-logout" @click="handleLogout">
          Déconnexion
        </button>
      </div>
    </aside>

    <!-- Contenu principal -->
    <main class="main-content">
      <slot />
    </main>

  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => {
  if (!authStore.user) return ''
  return `${authStore.user.prenom} ${authStore.user.nom}`
})

const userInitials = computed(() => {
  if (!authStore.user) return '?'
  return `${authStore.user.prenom?.[0] ?? ''}${authStore.user.nom?.[0] ?? ''}`.toUpperCase()
})

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background: #f1f5f9;
  font-family: 'Segoe UI', sans-serif;
}

/* ── Sidebar ── */
.sidebar {
  width: 260px;
  flex-shrink: 0;
  background: linear-gradient(180deg, #0f2d6b 0%, #1a4fa0 60%, #0d7a3e 100%);
  display: flex;
  flex-direction: column;
  padding: 1.5rem 1rem;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0.5rem 1.5rem;
  border-bottom: 1px solid rgba(255,255,255,0.15);
  margin-bottom: 1.5rem;
}

.sidebar-logo {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.sidebar-title {
  color: white;
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

/* ── Navigation ── */
.sidebar-nav {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border-radius: 10px;
  color: rgba(255,255,255,0.75);
  text-decoration: none;
  font-size: 0.95rem;
  transition: all 0.2s;
}

.nav-item:hover {
  background: rgba(255,255,255,0.12);
  color: white;
}

.nav-item.router-link-active {
  background: rgba(255,255,255,0.2);
  color: white;
  font-weight: 600;
}

.nav-icon {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
}

/* ── Footer sidebar ── */
.sidebar-footer {
  border-top: 1px solid rgba(255,255,255,0.15);
  padding-top: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(255,255,255,0.25);
  color: white;
  font-weight: 700;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.user-name {
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  color: rgba(255,255,255,0.6);
  font-size: 0.75rem;
}

.btn-logout {
  width: 100%;
  padding: 0.6rem;
  background: rgba(255,255,255,0.1);
  color: rgba(255,255,255,0.8);
  border: 1px solid rgba(255,255,255,0.2);
  border-radius: 8px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-logout:hover {
  background: rgba(255,255,255,0.2);
  color: white;
}

/* ── Contenu principal ── */
.main-content {
  flex: 1;
  min-width: 0;
  padding: 2rem;
  overflow-y: auto;
}

/* ── Responsive mobile ── */
@media (max-width: 768px) {
  .sidebar {
    width: 70px;
  }
  .sidebar-title,
  .user-details,
  .btn-logout,
  nav .nav-item span:last-child {
    display: none;
  }
  .sidebar-header { justify-content: center; padding: 0.5rem 0 1.5rem; }
  .nav-item { justify-content: center; padding: 0.75rem; }
  .user-info { justify-content: center; }
}
</style>
