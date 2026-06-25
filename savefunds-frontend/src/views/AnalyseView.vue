<template>
  <AppLayout>
    <div class="analyse-page">
      <div class="page-header">
        <h1>Nouvelle Analyse</h1>
        <p class="subtitle">Simulez la faisabilité d'un prélèvement dirigeant</p>
      </div>

      <div v-if="loadingEntreprise" class="loading">
        <i class="pi pi-spin pi-spinner" style="font-size: 2rem" />
      </div>

      <div v-else-if="!entreprise" class="no-entreprise">
        <i class="pi pi-exclamation-triangle" style="font-size: 2rem; color: #f59e0b" />
        <p>Vous devez d'abord enregistrer votre entreprise avant de lancer une analyse.</p>
        <PvButton label="Enregistrer mon entreprise" icon="pi pi-building" @click="router.push('/entreprise')" />
      </div>

      <div v-else class="analyse-content">

        <!-- Récapitulatif entreprise -->
        <section class="entreprise-card">
          <div class="entreprise-info">
            <span class="entreprise-name">{{ entreprise.raisonSociale }}</span>
            <span class="entreprise-num">{{ entreprise.numeroEntreprise }}</span>
          </div>
          <div class="entreprise-stats">
            <div class="stat">
              <span class="stat-label">CA mensuel</span>
              <span class="stat-value">{{ formatEur(entreprise.chiffreAffairesMensuel) }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">Charges</span>
              <span class="stat-value">{{ formatEur(entreprise.chargesMensuelles) }}</span>
            </div>
            <div class="stat">
              <span class="stat-label">Trésorerie</span>
              <span class="stat-value">{{ formatEur(entreprise.tresorerie) }}</span>
            </div>
          </div>
        </section>

        <!-- Formulaire -->
        <section class="form-section">
          <h2 class="section-title">
            <i class="pi pi-euro" /> Montant à prélever
          </h2>

          <div class="form-field">
            <label for="montant">Montant souhaité (€) <span class="required">*</span></label>
            <PvInputNumber
              id="montant"
              v-model="montantSouhaite"
              mode="currency"
              currency="EUR"
              locale="fr-BE"
              :min="0.01"
              :max="999999999.99"
              :class="{ 'p-invalid': error }"
              placeholder="ex: 3 000,00 €"
            />
            <small v-if="error" class="error">{{ error }}</small>
          </div>
        </section>

        <!-- Étapes en cours -->
        <div v-if="step !== 'idle'" class="steps-progress">
          <div class="step" :class="stepClass('creating')">
            <i :class="stepIcon('creating')" />
            <span>Création de l'analyse</span>
          </div>
          <div class="step-arrow">→</div>
          <div class="step" :class="stepClass('analysing')">
            <i :class="stepIcon('analysing')" />
            <span>Calcul des indicateurs</span>
          </div>
          <div class="step-arrow">→</div>
          <div class="step" :class="stepClass('done')">
            <i :class="stepIcon('done')" />
            <span>Décision générée</span>
          </div>
        </div>

        <PvMessage v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</PvMessage>

        <!-- Actions -->
        <div class="form-actions">
          <PvButton
            label="Lancer l'analyse"
            icon="pi pi-play"
            :loading="step !== 'idle'"
            :disabled="step !== 'idle'"
            @click="lancerAnalyse"
          />
        </div>

      </div>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'
import { analyseService } from '@/services/analyse.service'
import { entrepriseService } from '@/services/entreprise.service'
import { useAuthStore } from '@/stores/auth.store'

const router = useRouter()
const authStore = useAuthStore()

type Step = 'idle' | 'creating' | 'analysing' | 'done'

const loadingEntreprise = ref(true)
const entreprise = ref<any>(null)
const montantSouhaite = ref<number | null>(null)
const error = ref('')
const errorMessage = ref('')
const step = ref<Step>('idle')

onMounted(async () => {
  try {
    const userId = authStore.user?.id
    if (userId) {
      entreprise.value = await entrepriseService.getByUserId(userId)
    }
  } catch (e: any) {
    if (e?.response?.status !== 404) {
      errorMessage.value = 'Erreur lors du chargement de votre entreprise.'
    }
  } finally {
    loadingEntreprise.value = false
  }
})

function validate(): boolean {
  error.value = ''
  if (!montantSouhaite.value || montantSouhaite.value <= 0) {
    error.value = 'Le montant doit être supérieur à 0'
    return false
  }
  if (montantSouhaite.value > 999999999.99) {
    error.value = 'Le montant est trop élevé'
    return false
  }
  return true
}

async function lancerAnalyse() {
  errorMessage.value = ''
  if (!validate()) return

  try {
    // Étape 1 — créer l'analyse
    step.value = 'creating'
    const analyse = await analyseService.create(entreprise.value.id, montantSouhaite.value!)

    // Étape 2 — lancer le calcul
    step.value = 'analysing'
    await analyseService.analyser(analyse.id)

    // Étape 3 — redirection vers résultat
    step.value = 'done'
    await new Promise(r => setTimeout(r, 600)) // petite pause visuelle
    router.push(`/resultat/${analyse.id}`)

  } catch (e: any) {
    step.value = 'idle'
    const msg = e?.response?.data?.message
    errorMessage.value = msg ?? 'Une erreur est survenue lors de l\'analyse.'
  }
}

function stepClass(s: Step) {
  const order: Step[] = ['idle', 'creating', 'analysing', 'done']
  const current = order.indexOf(step.value)
  const target = order.indexOf(s)
  if (s === step.value) return 'step-active'
  if (target < current) return 'step-done'
  return 'step-pending'
}

function stepIcon(s: Step) {
  if (s === step.value) return 'pi pi-spin pi-spinner'
  const order: Step[] = ['idle', 'creating', 'analysing', 'done']
  if (order.indexOf(s) < order.indexOf(step.value)) return 'pi pi-check'
  return 'pi pi-circle'
}

function formatEur(val: number | null) {
  if (val === null || val === undefined) return '—'
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(val)
}
</script>

<style scoped>
.analyse-page {
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
  color: var(--color-text, #1e293b);
  margin: 0 0 0.4rem 0;
}

.subtitle {
  color: #64748b;
  margin: 0;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 4rem;
}

.no-entreprise {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem;
  text-align: center;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  color: #64748b;
}

/* Carte entreprise */
.entreprise-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.25rem 1.5rem;
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.entreprise-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.entreprise-name {
  font-weight: 700;
  color: #1e293b;
  font-size: 1rem;
}

.entreprise-num {
  font-size: 0.8rem;
  color: #64748b;
}

.entreprise-stats {
  display: flex;
  gap: 2rem;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  text-align: right;
}

.stat-label {
  font-size: 0.75rem;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.stat-value {
  font-size: 0.95rem;
  font-weight: 600;
  color: #1e293b;
}

/* Formulaire */
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
  color: var(--color-primary, #1e3a5f);
  margin: 0 0 1.25rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-field label {
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
}

.form-field :deep(.p-inputnumber-input) {
  width: 100%;
  font-size: 1.2rem;
}

.required { color: #ef4444; }
.error { color: #ef4444; font-size: 0.8rem; }

/* Étapes */
.steps-progress {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 1rem 1.5rem;
}

.step {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.85rem;
  font-weight: 500;
  color: #94a3b8;
}

.step-active {
  color: #1e3a5f;
}

.step-done {
  color: #16a34a;
}

.step-pending {
  color: #cbd5e1;
}

.step-arrow {
  color: #cbd5e1;
  font-size: 1rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
}
</style>
