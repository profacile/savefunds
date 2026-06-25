# SFB-10 : Grille de Décision "Feu Tricolore" SaveFunds

## 📊 CRITÈRES D'ANALYSE

### 1. Trésorerie disponible

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| > 4 mois de charges | 2 à 4 mois de charges | < 2 mois de charges |

**Calcul** : Trésorerie (€) ÷ Charges mensuelles (€) = X mois

**Exemple** : 15 000 € ÷ 2 000 € = 7.5 mois → 🟢 VERT

---

### 2. Compte courant dirigeant

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| Créditeur (≥ 0 €) | Débiteur < 3 mois de charges | Débiteur ≥ 3 mois de charges |

**Calcul** : Solde compte courant (€) ÷ Charges mensuelles (€) = X mois

**Exemple** : 
- 0 € → 🟢 VERT
- -5 000 € avec charges 2 000 € = 2.5 mois → 🟡 ORANGE
- -8 000 € avec charges 2 000 € = 4 mois → 🔴 ROUGE

---

### 3. Délai avant clôture d'exercice

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| > 6 mois | 3 à 6 mois | < 3 mois |

**Calcul** : Nombre de mois entre aujourd'hui et date de clôture

**Exemple** : Clôture 31/12, on est en avril → 8 mois → 🟢 VERT

---

### 4. Ratio prélèvement / CA annuel

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| < 30% | 30% à 50% | > 50% |

**Calcul** : (Prélèvement × 12) ÷ (CA mensuel × 12) × 100

**Exemple** : Prélèvement 3 000 €, CA mensuel 6 000 €
- (3 000 × 12) ÷ (6 000 × 12) = 36 000 ÷ 72 000 = 50% → 🟡 ORANGE

**Note** : Si prélèvement ponctuel (1 fois/an), moins critique

---

### 5. Montant prélèvement vs Trésorerie actuelle

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| < 20% de la trésorerie | 20% à 40% | > 40% |

**Calcul** : Prélèvement (€) ÷ Trésorerie (€) × 100

**Exemple** : 3 000 € ÷ 15 000 € = 20% → 🟡 ORANGE

---

### 6. Capacité de remboursement (si compte débiteur)

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| < 3 mois | 3 à 6 mois | > 6 mois |

**Calcul** : Montant débiteur (€) ÷ Bénéfice mensuel (€)

**Bénéfice mensuel** = CA mensuel - Charges mensuelles

**Exemple** : 
- Débiteur : 5 000 €
- Bénéfice : 6 000 € - 2 000 € = 4 000 €/mois
- Capacité : 5 000 ÷ 4 000 = 1.25 mois → 🟢 VERT

---

### 7. Fréquence des prélèvements

| 🟢 VERT | 🟡 ORANGE | 🔴 ROUGE |
|---------|-----------|----------|
| Ponctuel (< 1x/trimestre) | Occasionnel (1x/mois) | Fréquent (hebdo/bi-mensuel) |

**Risque** : Prélèvements fréquents = risque requalification en rémunération

---

## ⚠️ CRITÈRE CRITIQUE : Combinaison Compte Débiteur + Clôture proche

| Situation | Statut |
|-----------|--------|
| Compte débiteur ET < 3 mois avant clôture | 🔴 **ROUGE URGENT** |
| Compte débiteur ET 3-6 mois avant clôture | 🟡 **ORANGE - Agir vite** |
| Compte débiteur ET > 6 mois avant clôture | 🟡 **ORANGE - Planifier** |

**Exemple** : 
- Compte -10 000 €, clôture dans 2 mois → 🔴 **URGENCE FISCALE (ATN certain)**

---

## 🚦 DÉCISION GLOBALE

### Règle de calcul

1. **Compter le nombre de ROUGES** parmi les critères 1 à 7
2. **Vérifier le critère critique** (combinaison compte débiteur + clôture)

### Décision finale

| Nombre de 🔴 ROUGES | Décision |
|---------------------|----------|
| **0** | 🟢 **VERT - Prélèvement autorisé** |
| **1 rouge** (critère non-critique) | 🟡 **ORANGE - Possible avec vigilance** |
| **1 rouge critique** (Trésorerie OU Compte courant OU Combinaison) | 🔴 **ROUGE - Déconseillé** |
| **2+ rouges** | 🔴 **ROUGE - Interdit** |

---

## 📋 EXEMPLES CONCRETS

### Exemple 1 : Situation Saine (Marc - Cas A)

| Critère | Valeur | Statut |
|---------|--------|--------|
| Trésorerie après | 6 mois | 🟢 |
| Compte courant | Débiteur 1.5 mois | 🟡 |
| Délai clôture | 8 mois | 🟢 |
| Ratio CA | 50% (ponctuel) | 🟡 |
| Montant/Tréso | 20% | 🟢 |

**Total** : 0 rouge, 2 oranges → **🟢 VERT - OK**

---

### Exemple 2 : Vigilance (Sophie - Cas B)

| Critère | Valeur | Statut |
|---------|--------|--------|
| Trésorerie après | 1.7 mois | 🟡 |
| Nature opération | Remboursement | 🟢 |
| Délai clôture | 5 mois | 🟡 |
| CA variable | ±60% | 🔴 |
| Bénéfice minimum | Perte possible | 🔴 |

**Total** : 2 rouges → **🔴 ROUGE - Limiter à 1 000 €**

---

### Exemple 3 : Danger (Jean - Cas C)

| Critère | Valeur | Statut |
|---------|--------|--------|
| Trésorerie | 1.7 mois | 🔴 |
| Compte courant | Débiteur 5 mois | 🔴 |
| Délai clôture | 2 mois | 🔴 |
| **Combinaison critique** | **Débiteur + < 3 mois** | **🔴 URGENT** |
| Capacité rembourser | 15 mois | 🔴 |

**Total** : 4 rouges + 1 critique → **🔴 ROUGE CRITIQUE - 0 € + Plan urgence**

---

## 💡 SEUILS DE CALCUL RAPIDES

| Indicateur | Formule simple |
|------------|----------------|
| **Trésorerie en mois** | Trésorerie ÷ Charges mensuelles |
| **Compte courant en mois** | \|Solde CC\| ÷ Charges mensuelles |
| **Trésorerie après prélèvement** | Trésorerie - Montant prélèvement |
| **Nouveau compte courant** | Solde CC - Montant prélèvement |
| **Bénéfice mensuel** | CA mensuel - Charges mensuelles |

---

## 📌 MESSAGES D'ALERTE

### 🟢 VERT
**✓ Situation saine, prélèvement autorisé**

Recommandations :
- Confirmer le montant et la date
- Si compte devient débiteur : prévoir remboursement avant clôture
- Documenter la nature de l'opération

---

### 🟡 ORANGE
**⚠ Vigilance requise**

Recommandations :
- Limiter le montant au strict nécessaire
- Renforcer la trésorerie rapidement
- Planifier un remboursement si compte débiteur
- Surveiller la situation de près

---

### 🔴 ROUGE
**🚫 Prélèvement déconseillé ou interdit**

Actions prioritaires :
- Si trésorerie < 2 mois : **NE PAS PRÉLEVER**
- Si compte débiteur + clôture < 3 mois : **REMBOURSEMENT URGENT**
- Consulter un comptable
- Plan de sauvetage financier
- Date limite d'action : [calculée selon clôture]

---

## ✅ UTILISATION POUR LES DÉVELOPPEURS

**Algorithme simplifié** :

```
1. Calculer tous les indicateurs
2. Positionner chaque critère (🟢🟡🔴)
3. Compter les rouges
4. Vérifier critère combiné
5. Décision finale selon règle
6. Générer message adapté
```

**Inputs nécessaires** :
- CA mensuel
- Charges mensuelles
- Trésorerie actuelle
- Solde compte courant
- Date clôture
- Montant prélèvement

**Output** :
- Décision : VERT / ORANGE / ROUGE
- Liste critères avec statut
- Message personnalisé
- Recommandations
