<template>
  <AppLayout>
    <div class="dashboard-page">

      <!-- En-tête de bienvenue -->
      <div class="welcome-header">
        <div>
          <h1>Bonjour, {{ authStore.user?.prenom || authStore.user?.nom || 'Dirigeant' }} 👋</h1>
          <p class="subtitle">Voici l'état financier de votre entreprise</p>
        </div>
        <PvButton
          label="Nouvelle analyse"
          icon="pi pi-play"
          @click="router.push('/analyse')"
        />
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-grid">
        <PvSkeleton height="120px" border-radius="12px" />
        <PvSkeleton height="120px" border-radius="12px" />
        <PvSkeleton height="120px" border-radius="12px" />
      </div>

      <template v-else>

        <!-- Pas d'entreprise -->
        <div v-if="!entreprise" class="empty-state">
          <i class="pi pi-building" style="font-size: 2.5rem; color: #94a3b8" />
          <h2>Bienvenue sur SaveFunds</h2>
          <p>Commencez par enregistrer votre entreprise pour accéder aux analyses.</p>
          <PvButton
            label="Enregistrer mon entreprise"
            icon="pi pi-building"
            @click="router.push('/entreprise')"
          />
        </div>

        <template v-else>

          <!-- Carte entreprise + dernière décision -->
          <div class="cards-grid">

            <!-- Carte entreprise -->
            <div class="card card-entreprise">
              <div class="card-icon">
                <i class="pi pi-building" />
              </div>
              <div class="card-content">
                <span class="card-label">Mon entreprise</span>
                <span class="card-title">{{ entreprise.raisonSociale }}</span>
                <span class="card-sub">{{ entreprise.numeroEntreprise }}</span>
              </div>
            </div>

            <!-- Carte dernière décision -->
            <div class="card card-decision" :class="decisionCardClass">
              <div class="card-icon">
                <i class="pi pi-chart-bar" />
              </div>
              <div class="card-content">
                <span class="card-label">Dernière décision</span>
                <div v-if="derniereDecision">
                  <BadgeDecision :decision="derniereDecision" class="badge-xl" />
                  <span class="card-sub">Analyse #{{ derniereAnalyseId }}</span>
                </div>
                <span v-else class="card-no-data">Aucune analyse effectuée</span>
              </div>
            </div>

            <!-- Carte trésorerie -->
            <div class="card">
              <div class="card-icon">
                <i class="pi pi-wallet" />
              </div>
              <div class="card-content">
                <span class="card-label">Trésorerie</span>
                <span class="card-title">{{ formatEur(entreprise.tresorerie) }}</span>
                <span class="card-sub">{{ formatEur(entreprise.chargesMensuelles) }} / mois de charges</span>
              </div>
            </div>

            <!-- Carte CA mensuel -->
            <div class="card">
              <div class="card-icon">
                <i class="pi pi-arrow-up-right" />
              </div>
              <div class="card-content">
                <span class="card-label">CA mensuel</span>
                <span class="card-title">{{ formatEur(entreprise.chiffreAffairesMensuel) }}</span>
                <span class="card-sub">Ratio {{ ratioCACharges }}</span>
              </div>
            </div>

          </div>

          <!-- Historique des analyses -->
          <div class="section" id="historique-content">
            <div class="section-header">
              <h2>Historique des analyses</h2>
              <div style="display:flex; gap:0.5rem;">
                <PvButton
                  label="Exporter CSV"
                  icon="pi pi-download"
                  severity="secondary"
                  size="small"
                  @click="exportCSV"
                />
                <PvButton
                  label="Exporter PDF"
                  icon="pi pi-file-pdf"
                  severity="secondary"
                  size="small"
                  :loading="exportingPdf"
                  @click="exportPDF"
                />
                <PvButton
                  label="Nouvelle analyse"
                  icon="pi pi-plus"
                  severity="secondary"
                  size="small"
                  @click="router.push('/analyse')"
                />
              </div>
            </div>

            <div v-if="analyses.length === 0" class="empty-analyses">
              <p>Aucune analyse effectuée. Lancez votre première analyse !</p>
            </div>

            <div v-else class="analyses-list">
              <div
                v-for="analyse in analyses"
                :key="analyse.id"
                class="analyse-row"
                @click="analyse.statut === 'TERMINEE' && router.push(`/resultat/${analyse.id}`)"
                :class="{ clickable: analyse.statut === 'TERMINEE' }"
              >
                <div class="analyse-info">
                  <span class="analyse-id">#{{ analyse.id }}</span>
                  <span class="analyse-montant">{{ formatEur(analyse.montantSouhaite) }}</span>
                  <span class="analyse-date">{{ formatDate(analyse.createdAt) }}</span>
                </div>
                <div class="analyse-right">
                  <span v-if="analyse.statut === 'EN_ATTENTE'" class="statut-badge statut-attente">
                    En attente
                  </span>
                  <BadgeDecision
                    v-else-if="analyse.statut === 'TERMINEE' && analyse.resultatDecision"
                    :decision="analyse.resultatDecision"
                  />
                  <span v-else class="statut-badge statut-terminee">Terminée</span>
                  <i v-if="analyse.statut === 'TERMINEE'" class="pi pi-chevron-right chevron" />
                </div>
              </div>
            </div>
          </div>

        </template>
      </template>

    </div>
  </AppLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/AppLayout.vue'
import { entrepriseService } from '@/services/entreprise.service'
import { analyseService } from '@/services/analyse.service'
import { useAuthStore } from '@/stores/auth.store'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const entreprise = ref<any>(null)
const analyses = ref<any[]>([])
const derniereDecision = ref<string | null>(null)
const derniereAnalyseId = ref<number | null>(null)
const exportingPdf = ref(false)


onMounted(async () => {
  try {
    const userId = authStore.user?.id
    if (!userId) return

    // Charger l'entreprise
    try {
      entreprise.value = await entrepriseService.getByUserId(userId)
    } catch (e: any) {
      if (e?.response?.status !== 404) throw e
      return
    }

    // Charger les analyses
    const rawAnalyses = await analyseService.getByEntreprise(entreprise.value.id)
    analyses.value = rawAnalyses

    // Charger les résultats pour les analyses terminées
    for (const analyse of analyses.value) {
      if (analyse.statut === 'TERMINEE') {
        try {
          const resultat = await analyseService.getResultat(analyse.id)
          analyse.resultatDecision = resultat.decisionGlobale
        } catch {}
      }
    }

    // Dernière décision = première analyse terminée (triées par date desc)
    const derniereTerminee = analyses.value.find(a => a.statut === 'TERMINEE' && a.resultatDecision)
    if (derniereTerminee) {
      derniereDecision.value = derniereTerminee.resultatDecision
      derniereAnalyseId.value = derniereTerminee.id
    }

  } catch (e) {
    console.error('Erreur chargement dashboard', e)
  } finally {
    loading.value = false
  }
})

function exportCSV() {
  if (analyses.value.length === 0) return

  const header = 'ID,Montant souhaité,Date,Statut,Décision'
  const rows = analyses.value.map(a =>
    `${a.id},${a.montantSouhaite},${formatDate(a.createdAt)},${a.statut},${a.resultatDecision || ''}`
  )
  const csv = [header, ...rows].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `SaveFunds_Historique_${new Date().toISOString().slice(0,10)}.csv`
  link.click()
  URL.revokeObjectURL(url)
}

async function exportPDF() {
  exportingPdf.value = true
  try {
    const html2pdf = (await import('html2pdf.js')).default
    const element = document.getElementById('historique-content')
    if (!element) return

    const opt = {
      margin: [10, 10, 10, 10] as [number, number, number, number],
      filename: `SaveFunds_Historique_${new Date().toISOString().slice(0, 10)}.pdf`,
      image: { type: 'jpeg' as const, quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true },
      jsPDF: { unit: 'mm' as const, format: 'a4' as const, orientation: 'portrait' as const }
    }
    await html2pdf().set(opt).from(element).save()
  } finally {
    exportingPdf.value = false
  }
}

const ratioCACharges = computed(() => {
  if (!entreprise.value?.chiffreAffairesMensuel || !entreprise.value?.chargesMensuelles) return '—'
  const ratio = entreprise.value.chiffreAffairesMensuel / entreprise.value.chargesMensuelles
  return ratio.toFixed(2)
})

const decisionCardClass = computed(() => {
  if (!derniereDecision.value) return ''
  return {
    'card-vert': derniereDecision.value === 'VERT',
    'card-orange': derniereDecision.value === 'ORANGE',
    'card-rouge': derniereDecision.value === 'ROUGE',
  }
})

function formatEur(val: number | null) {
  if (val === null || val === undefined) return '—'
  return new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' }).format(val)
}

function formatDate(val: string) {
  if (!val) return '—'
  return new Intl.DateTimeFormat('fr-BE', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(val))
}
</script>

<style scoped>
.dashboard-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 2rem;
}

/* Header */
.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
}

.welcome-header h1 {
  font-size: 1.8rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.3rem 0;
}

.subtitle {
  color: #64748b;
  margin: 0;
}

/* Loading */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.25rem;
  margin-bottom: 2rem;
}

/* Empty state */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 4rem 2rem;
  text-align: center;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.empty-state h2 {
  font-size: 1.3rem;
  color: #1e293b;
  margin: 0;
}

.empty-state p {
  color: #64748b;
  margin: 0;
}

/* Cards grid */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.25rem;
  margin-bottom: 2rem;
}

@media (max-width: 600px) {
  .cards-grid { grid-template-columns: 1fr; }
}

.card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 1.25rem;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.card-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  color: #1e3a5f;
  flex-shrink: 0;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  min-width: 0;
}

.card-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #94a3b8;
  font-weight: 600;
}

.card-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #1e293b;
}

.card-sub {
  font-size: 0.8rem;
  color: #64748b;
}

.card-no-data {
  font-size: 0.9rem;
  color: #94a3b8;
  font-style: italic;
}

/* Decision card colors */
.card-vert { border-color: #86efac; background: #f0fdf4; }
.card-vert .card-icon { background: #dcfce7; color: #15803d; }

.card-orange { border-color: #fed7aa; background: #fff7ed; }
.card-orange .card-icon { background: #ffedd5; color: #c2410c; }

.card-rouge { border-color: #fca5a5; background: #fef2f2; }
.card-rouge .card-icon { background: #fee2e2; color: #b91c1c; }

.badge-xl :deep(.badge-decision) {
  font-size: 0.9rem;
  padding: 0.4rem 1rem;
  margin-bottom: 0.25rem;
}

/* Section historique */
.section {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid #f1f5f9;
}

.section-header h2 {
  font-size: 1rem;
  font-weight: 600;
  color: #1e3a5f;
  margin: 0;
}

.empty-analyses {
  padding: 2rem;
  text-align: center;
  color: #94a3b8;
  font-size: 0.9rem;
}

.analyses-list {
  divide-y: 1px solid #f1f5f9;
}

.analyse-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f8fafc;
  transition: background 0.15s;
}

.analyse-row.clickable {
  cursor: pointer;
}

.analyse-row.clickable:hover {
  background: #f8fafc;
}

.analyse-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.analyse-id {
  font-size: 0.85rem;
  font-weight: 600;
  color: #94a3b8;
  min-width: 40px;
}

.analyse-montant {
  font-weight: 600;
  color: #1e293b;
}

.analyse-date {
  font-size: 0.82rem;
  color: #94a3b8;
}

.analyse-right {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.statut-badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.25rem 0.6rem;
  border-radius: 999px;
}

.statut-attente {
  background: #fef9c3;
  color: #854d0e;
}

.statut-terminee {
  background: #f1f5f9;
  color: #475569;
}

.chevron {
  font-size: 0.75rem;
  color: #94a3b8;
}
</style>
