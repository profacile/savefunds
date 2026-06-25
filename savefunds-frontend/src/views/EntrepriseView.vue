<template>
  <AppLayout>
    <div class="entreprise-page">
      <div class="page-header">
        <h1>Mon Entreprise</h1>
        <p class="subtitle">
          {{ entrepriseId ? 'Mettez à jour les informations de votre entreprise' : 'Enregistrez votre entreprise pour commencer' }}
        </p>
      </div>

      <div v-if="loading" class="loading">
        <i class="pi pi-spin pi-spinner" style="font-size: 2rem" />
      </div>

      <form v-else @submit.prevent="submit" class="entreprise-form">

        <!-- Informations générales -->
        <section class="form-section">
          <h2 class="section-title">
            <i class="pi pi-building" /> Informations générales
          </h2>

          <div class="form-grid">
            <div class="form-field">
              <label for="raisonSociale">Raison sociale <span class="required">*</span></label>
              <PvInputText
                id="raisonSociale"
                v-model="form.raisonSociale"
                placeholder="ex: Profacile SRL"
                :class="{ 'p-invalid': errors.raisonSociale }"
              />
              <small v-if="errors.raisonSociale" class="error">{{ errors.raisonSociale }}</small>
            </div>

            <div class="form-field">
              <label for="numeroEntreprise">
                Numéro d'entreprise <span class="required">*</span>
                <span v-if="entrepriseId" class="locked-hint"><i class="pi pi-lock" /> non modifiable</span>
              </label>
              <PvInputText
                id="numeroEntreprise"
                v-model="form.numeroEntreprise"
                placeholder="ex: BE 0123.456.789"
                :disabled="!!entrepriseId"
                :class="{ 'p-invalid': errors.numeroEntreprise, 'field-locked': !!entrepriseId }"
              />
              <small v-if="errors.numeroEntreprise" class="error">{{ errors.numeroEntreprise }}</small>
            </div>

            <div class="form-field">
              <label for="formeJuridique">Forme juridique</label>
              <PvSelect
                id="formeJuridique"
                v-model="form.formeJuridique"
                :options="formesJuridiques"
                placeholder="Sélectionnez..."
                showClear
              />
            </div>

            <div class="form-field">
              <label for="secteurActivite">Secteur d'activité</label>
              <PvInputText
                id="secteurActivite"
                v-model="form.secteurActivite"
                placeholder="ex: Services informatiques"
              />
            </div>
          </div>
        </section>

        <!-- Données financières -->
        <section class="form-section">
          <h2 class="section-title">
            <i class="pi pi-euro" /> Données financières mensuelles
          </h2>

          <div class="form-grid">
            <div class="form-field">
              <label for="chiffreAffairesMensuel">Chiffre d'affaires mensuel (€) <span class="required">*</span></label>
              <PvInputNumber
                id="chiffreAffairesMensuel"
                v-model="form.chiffreAffairesMensuel"
                mode="currency"
                currency="EUR"
                locale="fr-BE"
                :min="0.01"
                :class="{ 'p-invalid': errors.chiffreAffairesMensuel }"
              />
              <small v-if="errors.chiffreAffairesMensuel" class="error">{{ errors.chiffreAffairesMensuel }}</small>
            </div>

            <div class="form-field">
              <label for="chargesMensuelles">Charges mensuelles (€) <span class="required">*</span></label>
              <PvInputNumber
                id="chargesMensuelles"
                v-model="form.chargesMensuelles"
                mode="currency"
                currency="EUR"
                locale="fr-BE"
                :min="0.01"
                :class="{ 'p-invalid': errors.chargesMensuelles }"
              />
              <small v-if="errors.chargesMensuelles" class="error">{{ errors.chargesMensuelles }}</small>
            </div>

            <div class="form-field">
              <label for="tresorerie">Trésorerie disponible (€) <span class="required">*</span></label>
              <PvInputNumber
                id="tresorerie"
                v-model="form.tresorerie"
                mode="currency"
                currency="EUR"
                locale="fr-BE"
                :min="0"
                :class="{ 'p-invalid': errors.tresorerie }"
              />
              <small v-if="errors.tresorerie" class="error">{{ errors.tresorerie }}</small>
            </div>

            <div class="form-field">
              <label for="soldeCompteCourant">Solde compte courant (€)</label>
              <PvInputNumber
                id="soldeCompteCourant"
                v-model="form.soldeCompteCourant"
                mode="currency"
                currency="EUR"
                locale="fr-BE"
              />
              <small class="hint">Peut être négatif</small>
            </div>
          </div>
        </section>

        <!-- Actions -->
        <div class="form-actions">
          <PvMessage v-if="successMessage" severity="success" :closable="false">{{ successMessage }}</PvMessage>
          <PvMessage v-if="errorMessage" severity="error" :closable="false">{{ errorMessage }}</PvMessage>

          <PvButton
            type="submit"
            :label="entrepriseId ? 'Mettre à jour' : 'Enregistrer'"
            :icon="entrepriseId ? 'pi pi-check' : 'pi pi-save'"
            :loading="submitting"
          />
        </div>

      </form>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import { entrepriseService } from '@/services/entreprise.service'
import { useAuthStore } from '@/stores/auth.store'

const authStore = useAuthStore()

const loading = ref(true)
const submitting = ref(false)
const entrepriseId = ref<number | null>(null)
const successMessage = ref('')
const errorMessage = ref('')

const formesJuridiques = ['SRL', 'SA', 'ASBL', 'SNC', 'SCS', 'SC', 'Autre']

const form = reactive({
  raisonSociale: '',
  numeroEntreprise: '',
  formeJuridique: '',
  secteurActivite: '',
  chiffreAffairesMensuel: null as number | null,
  chargesMensuelles: null as number | null,
  tresorerie: null as number | null,
  soldeCompteCourant: null as number | null,
})

const errors = reactive({
  raisonSociale: '',
  numeroEntreprise: '',
  chiffreAffairesMensuel: '',
  chargesMensuelles: '',
  tresorerie: '',
})

onMounted(async () => {
  try {
    const userId = authStore.user?.id
    if (!userId) return

    const entreprise = await entrepriseService.getByUserId(userId)
    entrepriseId.value = entreprise.id

    form.raisonSociale = entreprise.raisonSociale ?? ''
    form.numeroEntreprise = entreprise.numeroEntreprise ?? ''
    form.formeJuridique = entreprise.formeJuridique ?? ''
    form.secteurActivite = entreprise.secteurActivite ?? ''
    form.chiffreAffairesMensuel = entreprise.chiffreAffairesMensuel ?? null
    form.chargesMensuelles = entreprise.chargesMensuelles ?? null
    form.tresorerie = entreprise.tresorerie ?? null
    form.soldeCompteCourant = entreprise.soldeCompteCourant ?? null
  } catch (e: any) {
      // 404 = pas d'entreprise encore, silencieux
      // Les autres erreurs sont gérées par l'intercepteur global
  } finally {
    loading.value = false
  }
})

function validate(): boolean {
  let valid = true
  errors.raisonSociale = ''
  errors.numeroEntreprise = ''
  errors.chiffreAffairesMensuel = ''
  errors.chargesMensuelles = ''
  errors.tresorerie = ''

  if (!form.raisonSociale.trim()) {
    errors.raisonSociale = 'La raison sociale est obligatoire'
    valid = false
  }
  if (!entrepriseId.value && !form.numeroEntreprise.trim()) {
    errors.numeroEntreprise = "Le numéro d'entreprise est obligatoire"
    valid = false
  }
  if (!form.chiffreAffairesMensuel || form.chiffreAffairesMensuel <= 0) {
    errors.chiffreAffairesMensuel = "Le chiffre d'affaires doit être supérieur à 0"
    valid = false
  }
  if (!form.chargesMensuelles || form.chargesMensuelles <= 0) {
    errors.chargesMensuelles = 'Les charges doivent être supérieures à 0'
    valid = false
  }
  if (form.tresorerie === null || form.tresorerie < 0) {
    errors.tresorerie = 'La trésorerie doit être positive ou nulle'
    valid = false
  }
  return valid
}

async function submit() {
  successMessage.value = ''
  errorMessage.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    if (entrepriseId.value) {
      await entrepriseService.update(entrepriseId.value, {
        raisonSociale: form.raisonSociale,
        formeJuridique: form.formeJuridique || null,
        secteurActivite: form.secteurActivite || null,
        chiffreAffairesMensuel: form.chiffreAffairesMensuel,
        chargesMensuelles: form.chargesMensuelles,
        tresorerie: form.tresorerie,
        soldeCompteCourant: form.soldeCompteCourant,
      })
      successMessage.value = 'Entreprise mise à jour avec succès.'
    } else {
      const created = await entrepriseService.create({
        userId: authStore.user?.id,
        raisonSociale: form.raisonSociale,
        numeroEntreprise: form.numeroEntreprise,
        formeJuridique: form.formeJuridique || null,
        secteurActivite: form.secteurActivite || null,
        chiffreAffairesMensuel: form.chiffreAffairesMensuel,
        chargesMensuelles: form.chargesMensuelles,
        tresorerie: form.tresorerie,
        soldeCompteCourant: form.soldeCompteCourant,
      })
      entrepriseId.value = created.id
      successMessage.value = 'Entreprise enregistrée avec succès.'
    }
  } catch (e: any) {
    const msg = e?.response?.data?.message
    errorMessage.value = msg ?? 'Une erreur est survenue. Veuillez réessayer.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.entreprise-page {
  max-width: 860px;
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
  color: var(--color-text-muted, #64748b);
  margin: 0;
}

.loading {
  display: flex;
  justify-content: center;
  padding: 4rem;
  color: var(--color-primary, #1e3a5f);
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
  color: var(--color-primary, #1e3a5f);
  margin: 0 0 1.25rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.25rem;
}

@media (max-width: 600px) {
  .form-grid { grid-template-columns: 1fr; }
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
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.required { color: #ef4444; }

.locked-hint {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 400;
}

.field-locked { opacity: 0.6; }
.error { color: #ef4444; font-size: 0.8rem; }
.hint { color: #94a3b8; font-size: 0.8rem; }

.form-field :deep(.p-inputtext),
.form-field :deep(.p-inputnumber-input),
.form-field :deep(.p-select) {
  width: 100%;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: flex-start;
}
</style>
