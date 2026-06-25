<template>
  <AppLayout>
    <div class="profil-page">

      <div class="page-header">
        <h1>Mon Profil</h1>
        <p class="subtitle">Informations de votre compte</p>
      </div>

      <!-- Carte infos utilisateur -->
      <section class="form-section">
        <h2 class="section-title">
          <i class="pi pi-user" /> Informations personnelles
        </h2>

        <div class="user-card">
          <div class="user-avatar-lg">{{ userInitials }}</div>
          <div class="user-details-grid">
            <div class="detail-field">
              <span class="detail-label">Prénom</span>
              <span class="detail-value">{{ authStore.user?.prenom ?? '—' }}</span>
            </div>
            <div class="detail-field">
              <span class="detail-label">Nom</span>
              <span class="detail-value">{{ authStore.user?.nom ?? '—' }}</span>
            </div>
            <div class="detail-field full-width">
              <span class="detail-label">Adresse email</span>
              <span class="detail-value">{{ authStore.user?.email ?? '—' }}</span>
            </div>
            <div class="detail-field">
              <span class="detail-label">Rôle</span>
              <span class="role-badge">{{ authStore.user?.role ?? 'DIRIGEANT' }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Formulaire changement mot de passe -->
      <section class="form-section">
        <h2 class="section-title">
          <i class="pi pi-lock" /> Changer le mot de passe
        </h2>

        <form @submit.prevent="changerMotDePasse" class="password-form">
          <div class="form-grid">

            <div class="form-field full-width">
              <label for="ancienMdp">Mot de passe actuel <span class="required">*</span></label>
              <PvPassword
                id="ancienMdp"
                v-model="form.ancienMotDePasse"
                :feedback="false"
                toggleMask
                :class="{ 'p-invalid': errors.ancienMotDePasse }"
                placeholder="Votre mot de passe actuel"
              />
              <small v-if="errors.ancienMotDePasse" class="error">{{ errors.ancienMotDePasse }}</small>
            </div>

            <div class="form-field">
              <label for="nouveauMdp">Nouveau mot de passe <span class="required">*</span></label>
              <PvPassword
                id="nouveauMdp"
                v-model="form.nouveauMotDePasse"
                toggleMask
                :class="{ 'p-invalid': errors.nouveauMotDePasse }"
                placeholder="Min. 6 caractères"
              />
              <small v-if="errors.nouveauMotDePasse" class="error">{{ errors.nouveauMotDePasse }}</small>
            </div>

            <div class="form-field">
              <label for="confirmMdp">Confirmation <span class="required">*</span></label>
              <PvPassword
                id="confirmMdp"
                v-model="form.confirmationMotDePasse"
                :feedback="false"
                toggleMask
                :class="{ 'p-invalid': errors.confirmationMotDePasse }"
                placeholder="Répétez le nouveau mot de passe"
              />
              <small v-if="errors.confirmationMotDePasse" class="error">{{ errors.confirmationMotDePasse }}</small>
            </div>

          </div>

          <div class="form-actions">
            <PvMessage v-if="successMessage" severity="success" :closable="false">{{ successMessage }}</PvMessage>
            <PvMessage v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</PvMessage>

            <PvButton
              type="submit"
              label="Modifier le mot de passe"
              icon="pi pi-check"
              :loading="submitting"
            />
          </div>
        </form>
      </section>

    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import { useAuthStore } from '@/stores/auth.store'
import api from '@/services/api'

const authStore = useAuthStore()

const submitting = ref(false)
const successMessage = ref('')
const errorMessage = ref('')

const form = reactive({
  ancienMotDePasse: '',
  nouveauMotDePasse: '',
  confirmationMotDePasse: '',
})

const errors = reactive({
  ancienMotDePasse: '',
  nouveauMotDePasse: '',
  confirmationMotDePasse: '',
})

const userInitials = computed(() => {
  const p = authStore.user?.prenom?.[0] ?? ''
  const n = authStore.user?.nom?.[0] ?? ''
  return (p + n).toUpperCase() || '?'
})

function validate(): boolean {
  errors.ancienMotDePasse = ''
  errors.nouveauMotDePasse = ''
  errors.confirmationMotDePasse = ''
  let valid = true

  if (!form.ancienMotDePasse) {
    errors.ancienMotDePasse = 'L\'ancien mot de passe est obligatoire'
    valid = false
  }

  if (!form.nouveauMotDePasse || form.nouveauMotDePasse.length < 6) {
    errors.nouveauMotDePasse = 'Le nouveau mot de passe doit contenir au moins 6 caractères'
    valid = false
  }

  if (!form.confirmationMotDePasse) {
    errors.confirmationMotDePasse = 'La confirmation est obligatoire'
    valid = false
  } else if (form.nouveauMotDePasse !== form.confirmationMotDePasse) {
    errors.confirmationMotDePasse = 'Les mots de passe ne correspondent pas'
    valid = false
  }

  return valid
}

async function changerMotDePasse() {
  successMessage.value = ''
  errorMessage.value = ''

  if (!validate()) return

  submitting.value = true
  try {
    const userId = authStore.user?.id
    await api.put(`/api/v1/users/${userId}/password`, {
      ancienMotDePasse: form.ancienMotDePasse,
      nouveauMotDePasse: form.nouveauMotDePasse,
      confirmationMotDePasse: form.confirmationMotDePasse,
    })

    successMessage.value = 'Mot de passe modifié avec succès.'
    form.ancienMotDePasse = ''
    form.nouveauMotDePasse = ''
    form.confirmationMotDePasse = ''
  } catch (e: any) {
    const msg = e?.response?.data?.message
    errorMessage.value = msg ?? 'Une erreur est survenue.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.profil-page {
  max-width: 700px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.4rem 0;
}

.subtitle {
  color: #64748b;
  margin: 0;
}

.form-section {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1e3a5f;
  margin: 0 0 1.25rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* User card */
.user-card {
  display: flex;
  align-items: flex-start;
  gap: 1.5rem;
}

.user-avatar-lg {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1e3a5f, #1a4fa0);
  color: white;
  font-weight: 700;
  font-size: 1.3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  flex: 1;
}

.detail-field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-field.full-width {
  grid-column: 1 / -1;
}

.detail-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #94a3b8;
  font-weight: 600;
}

.detail-value {
  font-size: 0.95rem;
  font-weight: 500;
  color: #1e293b;
}

.role-badge {
  display: inline-block;
  font-size: 0.75rem;
  font-weight: 700;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  width: fit-content;
}

/* Password form */
.password-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
}

@media (max-width: 600px) {
  .form-grid { grid-template-columns: 1fr; }
  .user-card { flex-direction: column; align-items: center; }
  .user-details-grid { grid-template-columns: 1fr; }
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-field.full-width {
  grid-column: 1 / -1;
}

.form-field label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.form-field :deep(.p-password) {
  width: 100%;
}

.form-field :deep(.p-password-input) {
  width: 100%;
}

.required { color: #ef4444; }
.error { color: #ef4444; font-size: 0.8rem; }

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: flex-start;
}
</style>
