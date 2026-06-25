<template>
  <div class="login-page">

    <!-- Panneau gauche — branding -->
    <div class="brand-panel">
      <div class="brand-content">
        <img src="@/assets/logo-savefunds.jpeg" alt="SaveFunds" class="logo-main" />
        <h1>SaveFunds</h1>
        <p class="tagline">Votre vigilance financière intelligente<br/>pour PME/SRL belges</p>
        <div class="features">
          <div class="feature-item">
            <span class="dot green"></span>
            Surveillance de trésorerie en temps réel
          </div>
          <div class="feature-item">
            <span class="dot orange"></span>
            Alertes tricolores VERT / ORANGE / ROUGE
          </div>
          <div class="feature-item">
            <span class="dot blue"></span>
            Analyse automatique de vos indicateurs
          </div>
        </div>
        <div class="powered-by">
          <span>Propulsé par</span>
          <img src="@/assets/logo-profacile.jpeg" alt="Profacile" class="logo-profacile" />
        </div>
      </div>
    </div>

    <!-- Panneau droit — formulaire -->
    <div class="form-panel">
      <div class="form-card">
        <h2>Bonjour !👋</h2>
        <p class="subtitle">Connectez-vous à votre espace SaveFunds</p>

        <div v-if="errorMessage" class="error-box">
          ⚠️ {{ errorMessage }}
        </div>

        <div class="field">
          <label>Adresse email</label>
          <input
            v-model="email"
            type="email"
            placeholder="vous@entreprise.be"
            :disabled="loading"
            @keyup.enter="handleLogin"
          />
        </div>

        <div class="field">
          <label>Mot de passe</label>
          <input
            v-model="password"
            type="password"
            placeholder="••••••••"
            :disabled="loading"
            @keyup.enter="handleLogin"
          />
        </div>

        <button class="btn-login" @click="handleLogin" :disabled="loading">
          <span v-if="!loading">Se connecter →</span>
          <span v-else class="spinner">Connexion en cours...</span>
        </button>

        <div class="divider"><span>ou</span></div>

        <p class="register-link">
          Pas encore de compte ?
          <RouterLink to="/register">Créer un compte</RouterLink>
        </p>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { authService } from '@/services/auth.service'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

async function handleLogin() {
  errorMessage.value = ''
  if (!email.value || !password.value) {
    errorMessage.value = 'Veuillez remplir tous les champs.'
    return
  }
  loading.value = true
  try {
    const data = await authService.login(email.value, password.value)
    authStore.setAuth(data.token, data.user)
    router.push('/dashboard')
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Email ou mot de passe incorrect.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.login-page {
  display: flex;
  min-height: 100vh;
  font-family: 'Segoe UI', sans-serif;
}

/* ── Panneau gauche ── */
.brand-panel {
  flex: 0 0 420px;
  background: linear-gradient(145deg, #0f2d6b 0%, #1a4fa0 50%, #0d7a3e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 2rem;
  position: relative;
  overflow: hidden;
}

.brand-panel::before {
  content: '';
  position: absolute;
  width: 500px; height: 500px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
  top: -150px; right: -150px;
}

.brand-panel::after {
  content: '';
  position: absolute;
  width: 300px; height: 300px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
  bottom: -80px; left: -80px;
}

.brand-content {
  position: relative;
  z-index: 1;
  color: white;
  max-width: 400px;
  width: 100%;
}

.logo-main {
  width: 80px; height: 80px;
  border-radius: 16px;
  margin-bottom: 1.5rem;
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}

.brand-content h1 {
  font-size: clamp(1.8rem, 3vw, 2.8rem);
  font-weight: 700;
  letter-spacing: -1px;
  margin-bottom: 0.5rem;
}

.tagline {
  font-size: clamp(0.85rem, 1.5vw, 1.05rem);
  opacity: 0.85;
  line-height: 1.6;
  margin-bottom: 2.5rem;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
  margin-bottom: 3rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: clamp(0.8rem, 1.3vw, 0.95rem);
  opacity: 0.9;
}

.dot {
  width: 10px; height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot.green  { background: #22c55e; box-shadow: 0 0 8px #22c55e; }
.dot.orange { background: #f97316; box-shadow: 0 0 8px #f97316; }
.dot.blue   { background: #60a5fa; box-shadow: 0 0 8px #60a5fa; }

.powered-by {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  opacity: 0.7;
  font-size: 0.85rem;
  border-top: 1px solid rgba(255,255,255,0.2);
  padding-top: 1.5rem;
}

.logo-profacile {
  width: 36px; height: 36px;
  border-radius: 8px;
}

/* ── Panneau droit ── */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem 4rem;
  background: white;
  min-width: 0;
}

.form-card {
  width: 100%;
  max-width: 500px;
}

.form-card h2 {
  font-size: clamp(1.4rem, 2.5vw, 1.8rem);
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 0.4rem;
  white-space: nowrap;
}

.subtitle {
  color: #64748b;
  font-size: 0.95rem;
  margin-bottom: 2rem;
}

.error-box {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #dc2626;
  padding: 0.75rem 1rem;
  border-radius: 10px;
  font-size: 0.9rem;
  margin-bottom: 1.25rem;
}

.field { margin-bottom: 1.25rem; }

.field label {
  display: block;
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.4rem;
}

.field input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 0.95rem;
  background: white;
  transition: border-color 0.2s;
  color: #0f172a;
}

.field input:focus {
  outline: none;
  border-color: #1a4fa0;
  box-shadow: 0 0 0 3px rgba(26, 79, 160, 0.1);
}

.btn-login {
  width: 100%;
  padding: 0.85rem;
  background: linear-gradient(135deg, #1a4fa0, #0d7a3e);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 0.5rem;
  transition: opacity 0.2s, transform 0.1s;
}

.btn-login:hover  { opacity: 0.92; transform: translateY(-1px); }
.btn-login:active { transform: translateY(0); }
.btn-login:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

.divider {
  text-align: center;
  margin: 1.5rem 0;
  position: relative;
  color: #94a3b8;
  font-size: 0.85rem;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%; left: 0; right: 0;
  height: 1px;
  background: #e2e8f0;
}

.divider span {
  background: #f8fafc;
  padding: 0 0.75rem;
  position: relative;
}

.register-link {
  text-align: center;
  font-size: 0.9rem;
  color: #64748b;
}

.register-link a {
  color: #1a4fa0;
  font-weight: 600;
  text-decoration: none;
}
.register-link a:hover { text-decoration: underline; }

/* ── Responsive tablette ── */
@media (max-width: 900px) {
  .brand-panel { padding: 2rem 1.5rem; }
  .features { margin-bottom: 2rem; }
}

/* ── Responsive mobile ── */
@media (max-width: 640px) {
  .login-page { flex-direction: column; }
  .brand-panel {
    flex: none;
    padding: 2rem 1.5rem;
    min-height: auto;
  }
  .brand-content h1 { font-size: 1.6rem; }
  .tagline { margin-bottom: 1rem; }
  .features { margin-bottom: 1.5rem; }
  .form-panel { flex: none; min-height: auto; padding: 2rem 1.5rem; }
}
</style>
