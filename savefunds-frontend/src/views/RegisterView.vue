<template>
  <div class="login-page">

    <div class="brand-panel">
      <div class="brand-content">
        <img src="@/assets/logo-savefunds.jpeg" alt="SaveFunds" class="logo-main" />
        <h1>SaveFunds</h1>
        <p class="tagline">Créez votre compte et commencez à surveiller votre santé financière.</p>
        <div class="features">
          <div class="feature-item"><span class="dot green"></span>Inscription gratuite</div>
          <div class="feature-item"><span class="dot orange"></span>1 entreprise par compte</div>
          <div class="feature-item"><span class="dot blue"></span>Résultats en quelques secondes</div>
        </div>
        <div class="powered-by">
          <span>Propulsé par</span>
          <img src="@/assets/logo-profacile.jpeg" alt="Profacile" class="logo-profacile" />
        </div>
      </div>
    </div>

    <div class="form-panel">
      <div class="form-card">
        <h2>Créer un compte</h2>
        <p class="subtitle">Rejoignez SaveFunds dès aujourd'hui</p>

        <div v-if="errorMessage" class="error-box">⚠️ {{ errorMessage }}</div>

        <div class="fields-row">
          <div class="field">
            <label>Prénom</label>
            <input v-model="prenom" type="text" placeholder="Jean" :disabled="loading" />
          </div>
          <div class="field">
            <label>Nom</label>
            <input v-model="nom" type="text" placeholder="Dupont" :disabled="loading" />
          </div>
        </div>

        <div class="field">
          <label>Adresse email</label>
          <input v-model="email" type="email" placeholder="vous@entreprise.be" :disabled="loading" />
        </div>

        <div class="field">
          <label>Mot de passe</label>
          <input v-model="password" type="password" placeholder="••••••••" :disabled="loading" @keyup.enter="handleRegister" />
        </div>

        <button class="btn-login" @click="handleRegister" :disabled="loading">
          <span v-if="!loading">Créer mon compte →</span>
          <span v-else>Création en cours...</span>
        </button>

        <div class="divider"><span>ou</span></div>

        <p class="register-link">
          Déjà un compte ?
          <RouterLink to="/login">Se connecter</RouterLink>
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

const nom = ref('')
const prenom = ref('')
const email = ref('')
const password = ref('')
const loading = ref(false)
const errorMessage = ref('')

async function handleRegister() {
  errorMessage.value = ''
  if (!nom.value || !prenom.value || !email.value || !password.value) {
    errorMessage.value = 'Veuillez remplir tous les champs.'
    return
  }
  loading.value = true
  try {
    const data = await authService.register(email.value, password.value, nom.value, prenom.value)
    authStore.setAuth(data.token, data.user)
    router.push('/dashboard')
  } catch (error: any) {
    errorMessage.value = error.response?.data?.message || 'Une erreur est survenue.'
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

.brand-content {
  position: relative;
  z-index: 1;
  color: white;
  max-width: 340px;
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
  margin-bottom: 0.5rem;
}

.tagline {
  font-size: 0.95rem;
  opacity: 0.85;
  line-height: 1.6;
  margin-bottom: 2rem;
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
  font-size: 0.9rem;
  opacity: 0.9;
}

.dot {
  width: 10px; height: 10px;
  border-radius: 50%; flex-shrink: 0;
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

.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 4rem;
  background: white;
}

.form-card {
  width: 100%;
  max-width: 500px;
}

.form-card h2 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 0.4rem;
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

/* Ligne prénom + nom côte à côte */
.fields-row {
  display: flex;
  gap: 1rem;
}

.fields-row .field { flex: 1; }

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
  background: white;
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

@media (max-width: 640px) {
  .login-page { flex-direction: column; }
  .brand-panel { flex: none; width: 100%; padding: 2rem 1.5rem; }
  .form-panel  { flex: none; padding: 2rem 1.5rem; }
  .fields-row  { flex-direction: column; gap: 0; }
}
</style>
