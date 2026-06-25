<template>
  <AppLayout>
    <div class="resultat-page" id="resultat-content">

      <div v-if="loading" class="loading">
        <i class="pi pi-spin pi-spinner" style="font-size: 2rem" />
      </div>

      <div v-else-if="errorMessage" class="error-state">
        <i class="pi pi-exclamation-circle" style="font-size: 2rem; color: #ef4444" />
        <p>{{ errorMessage }}</p>
        <PvButton label="Nouvelle analyse" icon="pi pi-arrow-left" @click="router.push('/analyse')" />
      </div>

      <template v-else-if="resultat">

        <!-- En-tête décision globale -->
        <div class="resultat-header">
          <div class="header-left">
            <h1>Résultat de l'analyse</h1>
            <p class="analyse-meta">
              Analyse #{{ analyseId }} — montant demandé :
              <strong>{{ formatEur(analyse?.montantSouhaite) }}</strong>
            </p>
          </div>
          <div class="decision-globale">
            <span class="decision-label">Décision globale</span>
            <BadgeDecision :decision="resultat.decisionGlobale" class="badge-lg" />
          </div>
        </div>

        <!-- Résumé / recommandation globale -->
        <section v-if="resultat.recommandationGlobale" class="summary-card">
          <i class="pi pi-info-circle" />
          <p>{{ resultat.recommandationGlobale }}</p>
        </section>

        <!-- Montant max prélevable -->
        <section v-if="resultat.montantMaxPrelevable !== null" class="montant-max-card">
          <div class="montant-max-content">
            <div>
              <span class="montant-max-label">Montant demandé</span>
              <span class="montant-max-value">{{ formatEur(resultat.montantSouhaite) }}</span>
            </div>
            <div class="montant-max-separator">sur</div>
            <div>
              <span class="montant-max-label">Maximum prélevable</span>
              <span class="montant-max-value highlight">{{ formatEur(resultat.montantMaxPrelevable) }}</span>
            </div>
          </div>
        </section>

        <!-- 3 Indicateurs -->
        <div class="indicateurs-grid">

          <!-- Trésorerie -->
          <div class="indicateur-card">
            <div class="indicateur-header">
              <span class="indicateur-title">
                <i class="pi pi-wallet" /> Trésorerie
              </span>
              <BadgeDecision v-if="resultat.decisionTresorerie" :decision="resultat.decisionTresorerie" />
            </div>
            <div v-if="resultat.scoreTresorerie !== null" class="indicateur-score">
              {{ formatEur(resultat.scoreTresorerie) }}
            </div>
            <p v-if="resultat.detailsTresorerie" class="indicateur-details">
              {{ resultat.detailsTresorerie }}
            </p>
            <p v-if="resultat.recommandationTresorerie" class="indicateur-recommandation">
              <i class="pi pi-lightbulb" /> {{ resultat.recommandationTresorerie }}
            </p>
          </div>

          <!-- Ratio CA / Charges -->
          <div class="indicateur-card">
            <div class="indicateur-header">
              <span class="indicateur-title">
                <i class="pi pi-chart-bar" /> Ratio CA / Charges
              </span>
              <BadgeDecision v-if="resultat.decisionRatioCACharges" :decision="resultat.decisionRatioCACharges" />
            </div>
            <div v-if="resultat.scoreRatioCACharges !== null" class="indicateur-score">
              {{ formatPct(resultat.scoreRatioCACharges) }}
            </div>
            <p v-if="resultat.detailsRatioCACharges" class="indicateur-details">
              {{ resultat.detailsRatioCACharges }}
            </p>
            <p v-if="resultat.recommandationRatioCACharges" class="indicateur-recommandation">
              <i class="pi pi-lightbulb" /> {{ resultat.recommandationRatioCACharges }}
            </p>
          </div>

          <!-- Compte courant -->
          <div class="indicateur-card">
            <div class="indicateur-header">
              <span class="indicateur-title">
                <i class="pi pi-credit-card" /> Compte courant
              </span>
              <BadgeDecision v-if="resultat.decisionCompteCourant" :decision="resultat.decisionCompteCourant" />
            </div>
            <div v-if="resultat.scoreCompteCourantDebiteur !== null" class="indicateur-score">
              {{ resultat.scoreCompteCourantDebiteur > 0 ? `${resultat.scoreCompteCourantDebiteur} jours` : 'OK' }}
            </div>
            <p v-if="resultat.detailsCompteCourant" class="indicateur-details">
              {{ resultat.detailsCompteCourant }}
            </p>
            <p v-if="resultat.recommandationCompteCourant" class="indicateur-recommandation">
              <i class="pi pi-lightbulb" /> {{ resultat.recommandationCompteCourant }}
            </p>
          </div>

        </div>

        <!-- Actions -->
        <div class="form-actions">
          <PvButton
            label="Nouvelle analyse"
            icon="pi pi-plus"
            @click="router.push('/analyse')"
          />
          <PvButton
            label="Exporter PDF"
            icon="pi pi-file-pdf"
            severity="secondary"
            :loading="exportingPdf"
            @click="exportPDF"
          />
          <PvButton
            label="Imprimer"
            icon="pi pi-print"
            severity="secondary"
            @click="imprimer"
          />
          <PvButton
            label="Copier le lien"
            icon="pi pi-link"
            severity="secondary"
            @click="copierLien"
          />
          <PvButton
            label="Retour au dashboard"
            icon="pi pi-home"
            severity="secondary"
            @click="router.push('/dashboard')"
          />
        </div>

      </template>
    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'
import { analyseService } from '@/services/analyse.service'

const route = useRoute()
const router = useRouter()

const analyseId = route.params.id as string
const loading = ref(true)
const errorMessage = ref('')
const resultat = ref<any>(null)
const analyse = ref<any>(null)
const exportingPdf = ref(false)

onMounted(async () => {
  try {
    resultat.value = await analyseService.getResultat(Number(analyseId))
    analyse.value = await analyseService.getById(Number(analyseId))
  } catch (e: any) {
    errorMessage.value = e?.response?.data?.message ?? 'Impossible de charger le résultat.'
  } finally {
    loading.value = false
  }
})

async function exportPDF() {
  exportingPdf.value = true
  try {
    const html2pdf = (await import('html2pdf.js')).default
    const element = document.getElementById('resultat-content')
    if (!element) return

    const opt = {
      margin: [10, 10, 10, 10] as [number, number, number, number],
      filename: `SaveFunds_Analyse_${analyseId}_${new Date().toISOString().slice(0, 10)}.pdf`,
      image: { type: 'jpeg' as const, quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm' as const, format: 'a4' as const, orientation: 'portrait' as const }
    }
    await html2pdf().set(opt).from(element).save()
  } finally {
    exportingPdf.value = false
  }
}

function copierLien() {
  navigator.clipboard.writeText(window.location.href)
  alert('Lien copié dans le presse-papier !')
}

function imprimer() {
  window.print()
}

function formatEur(val: number | null) {
  if (val === null || val === undefined) return '—'
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(val)
}

function formatPct(val: number | null) {
  if (val === null || val === undefined) return '—'
  return `${Number(val).toFixed(1)} %`
}
</script>

<style scoped>
.resultat-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 2rem;
}

.montant-max-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 1rem 1.25rem;
  margin-bottom: 1.5rem;
}

.montant-max-content {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.montant-max-content > div {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.montant-max-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #94a3b8;
  font-weight: 600;
}

.montant-max-value {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
}

.montant-max-value.highlight {
  color: #0d7a3e;
}

.montant-max-separator {
  color: #94a3b8;
  font-size: 0.9rem;
}

.loading, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 4rem;
  text-align: center;
  color: #64748b;
}

.resultat-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.resultat-header h1 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.4rem 0;
}

.analyse-meta {
  color: #64748b;
  font-size: 0.9rem;
  margin: 0;
}

.decision-globale {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
}

.decision-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #94a3b8;
  font-weight: 600;
}

.badge-lg :deep(.badge-decision) {
  font-size: 1rem;
  padding: 0.5rem 1.25rem;
}

.summary-card {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 10px;
  padding: 1rem 1.25rem;
  margin-bottom: 1.5rem;
  color: #0369a1;
  font-size: 0.9rem;
  line-height: 1.6;
}

.summary-card i {
  margin-top: 2px;
  flex-shrink: 0;
}

.indicateurs-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.25rem;
  margin-bottom: 2rem;
}

@media (max-width: 768px) {
  .indicateurs-grid {
    grid-template-columns: 1fr;
  }
}

.indicateur-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.indicateur-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.indicateur-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1e3a5f;
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.indicateur-score {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
}

.indicateur-details {
  font-size: 0.85rem;
  color: #475569;
  line-height: 1.5;
  margin: 0;
}

.indicateur-recommandation {
  font-size: 0.82rem;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
  display: flex;
  align-items: flex-start;
  gap: 0.4rem;
  border-top: 1px solid #f1f5f9;
  padding-top: 0.5rem;
}

.indicateur-recommandation i {
  flex-shrink: 0;
  margin-top: 2px;
  color: #f59e0b;
}

.form-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}
</style>
