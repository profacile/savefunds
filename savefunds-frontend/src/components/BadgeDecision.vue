<template>
  <span class="badge-decision" :class="badgeClass">
    <span class="badge-dot" />
    {{ label }}
  </span>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  decision: string
}>()

const normalized = computed(() => props.decision?.toUpperCase())

const badgeClass = computed(() => ({
  'badge-vert':   normalized.value === 'VERT',
  'badge-orange': normalized.value === 'ORANGE',
  'badge-rouge':  normalized.value === 'ROUGE',
  'badge-unknown': !['VERT', 'ORANGE', 'ROUGE'].includes(normalized.value),
}))

const label = computed(() => {
  switch (normalized.value) {
    case 'VERT':   return 'VERT'
    case 'ORANGE': return 'ORANGE'
    case 'ROUGE':  return 'ROUGE'
    default:       return props.decision ?? '—'
  }
})
</script>

<style scoped>
.badge-decision {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.3rem 0.75rem;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.badge-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

/* VERT */
.badge-vert {
  background: #dcfce7;
  color: #15803d;
}
.badge-vert .badge-dot {
  background: #16a34a;
}

/* ORANGE */
.badge-orange {
  background: #fff7ed;
  color: #c2410c;
}
.badge-orange .badge-dot {
  background: #ea580c;
}

/* ROUGE */
.badge-rouge {
  background: #fee2e2;
  color: #b91c1c;
}
.badge-rouge .badge-dot {
  background: #dc2626;
}

/* Inconnu */
.badge-unknown {
  background: #f1f5f9;
  color: #64748b;
}
.badge-unknown .badge-dot {
  background: #94a3b8;
}
</style>
