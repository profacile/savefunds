# SFB-14 : Inputs Minimum MVP SaveFunds

## DONNÉES DE BASE (Profil Entreprise)

### 1. Informations entreprise
| Champ | Type | Requis | Validation | Exemple |
|-------|------|--------|------------|---------|
| Raison sociale | Texte | Oui | Max 100 char | "Consulting IT SPRL" |
| Numéro BCE | Texte | Non | Format BE0XXX.XXX.XXX | "BE0123.456.789" |
| Date de clôture exercice | Date | Oui | Format DD/MM | "31/12" |
| Capital social | Nombre (€) | Non | > 0 | 18 600 € |

### 2. Données financières courantes
| Champ | Type | Requis | Validation | Exemple | Notes |
|-------|------|--------|------------|---------|-------|
| **Chiffre d'affaires mensuel moyen** | Nombre (€) | Oui | > 0 | 6 000 € | Ou moyenne 3 derniers mois |
| **Charges fixes mensuelles** | Nombre (€) | Oui | > 0 | 2 000 € | Loyer + salaires + assurances + utilities |
| **Trésorerie disponible actuelle** | Nombre (€) | Oui | ≥ 0 | 15 000 € | Solde compte courant société |
| **Solde compte courant dirigeant** | Nombre (€) | Oui | Tout nombre | -5 000 € | Négatif = débiteur (dirigeant doit), Positif = créditeur (société doit) |
| Fonds propres | Nombre (€) | Non | Tout nombre | 25 000 € | Actif - Passif (peut être négatif) |

### 3. Informations dirigeant
| Champ | Type | Requis | Validation | Exemple |
|-------|------|--------|------------|---------|
| Nom complet | Texte | Oui | Max 100 char | "Marc Dubois" |
| Email | Email | Oui | Format email | "marc@email.com" |
| Téléphone | Texte | Non | Format BE | "+32 470 12 34 56" |

---

## DONNÉES DE L'OPÉRATION (Demande de prélèvement)

### 4. Détails du prélèvement envisagé
| Champ | Type | Requis | Validation | Exemple | Notes |
|-------|------|--------|------------|---------|-------|
| **Montant du prélèvement** | Nombre (€) | Oui | > 0 | 3 000 € | Montant exact souhaité |
| **Nature de l'opération** | Liste déroulante | Oui | Choix multiples | "Prélèvement personnel" | Options ci-dessous |
| Date prélèvement prévue | Date | Non | Futur uniquement | "20/03/2026" | Par défaut = aujourd'hui |
| Justification/Motif | Texte | Non | Max 200 char | "Travaux personnels" | Optionnel mais recommandé |

#### Options Nature de l'opération:
- **Prélèvement personnel** (avance au dirigeant)
- **Remboursement avance** (le dirigeant a prêté avant)
- **Dividende** (distribution bénéfices)
- **Rémunération supplémentaire** (bonus)
- **Autre** (à préciser)

---

## DONNÉES CALCULÉES (Auto-générées par SaveFunds)

### 5. Indicateurs dérivés
Ces données sont **calculées automatiquement** par l'application :

| Indicateur | Formule | Exemple |
|------------|---------|---------|
| Trésorerie en mois de charges | Trésorerie / Charges mensuelles | 15 000 / 2 000 = 7.5 mois |
| Compte courant en mois de charges | ABS(Solde CC) / Charges mensuelles | 5 000 / 2 000 = 2.5 mois |
| Mois avant clôture | Diff(Date clôture, Date actuelle) | 8 mois |
| Ratio prélèvement/CA annuel | (Prélèvement × 12) / (CA mensuel × 12) × 100 | (3 000 × 12) / (6 000 × 12) = 50% |
| Trésorerie restante après prélèvement | Trésorerie - Montant prélèvement | 15 000 - 3 000 = 12 000 € |
| Trésorerie restante en mois | Tréso restante / Charges mensuelles | 12 000 / 2 000 = 6 mois |
| Nouveau solde compte courant | Solde CC - Montant prélèvement | -5 000 - 3 000 = -8 000 € |
| Capacité remboursement (mois) | ABS(Nouveau solde CC) / (CA - Charges) | 8 000 / (6 000 - 2 000) = 2 mois |

---

## FORMULAIRE MVP - STRUCTURE PROPOSÉE

### Écran 1 : Profil entreprise (à remplir 1 fois)
```
┌─────────────────────────────────────────┐
│ PROFIL ENTREPRISE                       │
├─────────────────────────────────────────┤
│ Raison sociale: [________________]      │
│ Date clôture: [__/__]                   │
│                                         │
│ CA mensuel moyen: [________] €          │
│ Charges mensuelles: [________] €        │
│ Trésorerie actuelle: [________] €       │
│ Compte courant dirigeant: [________] €  │
│                                         │
│ [Enregistrer]                           │
└─────────────────────────────────────────┘
```

### Écran 2 : Demande d'analyse (à chaque prélèvement)
```
┌─────────────────────────────────────────┐
│ DEMANDE D'ANALYSE                       │
├─────────────────────────────────────────┤
│ Montant souhaité: [________] €          │
│                                         │
│ Nature: [Prélèvement personnel ▼]       │
│                                         │
│ Date: [__/__/____]                      │
│                                         │
│ Motif (optionnel):                      │
│ [________________________________]      │
│                                         │
│ [Analyser]                              │
└─────────────────────────────────────────┘
```

### Écran 3 : Résultat (généré par SaveFunds)
```
┌─────────────────────────────────────────┐
│ 🟢 PRÉLÈVEMENT AUTORISÉ                 │
├─────────────────────────────────────────┤
│ Montant: 3 000 €                        │
│                                         │
│ SITUATION APRÈS PRÉLÈVEMENT:            │
│ • Trésorerie: 12 000 € (6 mois)         │
│ • Compte courant: -8 000 €              │
│ • Délai clôture: 8 mois                 │
│                                         │
│ RECOMMANDATIONS:                        │
│ ✓ Situation saine                       │
│ ⚠ Prévoir remboursement avant clôture  │
│                                         │
│ [Confirmer prélèvement] [Modifier]      │
└─────────────────────────────────────────┘
```

---

## VALIDATION FRONTEND (Rules)

| Champ | Règle | Message erreur |
|-------|-------|----------------|
| CA mensuel | > 0 | "Le CA doit être supérieur à 0 €" |
| Charges | > 0 ET < CA × 2 | "Charges anormalement élevées par rapport au CA" |
| Trésorerie | ≥ 0 | "La trésorerie ne peut pas être négative" |
| Montant prélèvement | > 0 ET ≤ Trésorerie | "Montant supérieur à la trésorerie disponible" |
| Date clôture | Format valide | "Format attendu: JJ/MM" |

---

## STOCKAGE BASE DE DONNÉES

### Tables minimum

**entreprises**
- id, raison_sociale, date_cloture, capital_social, created_at, updated_at

**dirigeants**
- id, entreprise_id, nom, email, telephone, created_at

**situation_financiere** (historique)
- id, entreprise_id, date, ca_mensuel, charges_mensuelles, tresorerie, solde_compte_courant, fonds_propres

**prelevements** (historique demandes)
- id, entreprise_id, dirigeant_id, date_demande, montant, nature, justification, resultat_analyse (JSON), statut, created_at

---

## FORMAT API REST

### POST /api/prelevements/analyser

**Request:**
```json
{
  "entreprise_id": 123,
  "montant": 3000,
  "nature": "PRELEVEMENT_PERSONNEL",
  "date_prevue": "2026-03-20",
  "justification": "Travaux personnels",
  "situation_actuelle": {
    "ca_mensuel": 6000,
    "charges_mensuelles": 2000,
    "tresorerie": 15000,
    "solde_compte_courant": 0,
    "date_cloture": "12-31"
  }
}
```

**Response:**
```json
{
  "decision": "VERT",
  "score_global": 85,
  "indicateurs": {
    "tresorerie_restante": 12000,
    "tresorerie_en_mois": 6,
    "nouveau_solde_cc": -3000,
    "mois_avant_cloture": 8,
    "ratio_prelevement_ca": 50
  },
  "criteres": [
    {
      "nom": "Trésorerie",
      "statut": "VERT",
      "valeur": "6 mois"
    },
    {
      "nom": "Compte courant",
      "statut": "ORANGE",
      "valeur": "Débiteur 1.5 mois"
    }
  ],
  "recommandations": [
    "Situation saine après prélèvement",
    "Prévoir remboursement avant clôture (dans 8 mois)"
  ],
  "alertes": []
}
```

---

## ÉVOLUTIONS FUTURES (Post-MVP)

Inputs optionnels pour versions suivantes:
- TVA trimestrielle
- Impôts sociétés (provision)
- Rémunération mensuelle dirigeant
- Prêts bancaires (échéances)
- Investissements planifiés
- Historique 12 derniers mois (CA variable)

---

## RÉSUMÉ POUR DÉVELOPPEURS

**Inputs obligatoires minimum (5 champs):**
1. CA mensuel moyen
2. Charges mensuelles
3. Trésorerie actuelle
4. Solde compte courant dirigeant
5. Montant prélèvement souhaité

**Inputs recommandés (+ 2 champs):**
6. Date clôture exercice
7. Nature opération

**= 7 champs total pour MVP fonctionnel**

Tout le reste est calculé automatiquement par l'algorithme SaveFunds.
