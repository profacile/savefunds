# SaveFunds - Analyse Fonctionnelle Sprint 1

**Application de vigilance financière pour PME belges**

---

**Profacile SRL**  
**Sprint 1 - Phase Analyse**  
**Mars 2026**

---

## 📋 Table des matières

1. [Introduction](#1-introduction)
   - 1.1 [Objectif du document](#11-objectif-du-document)
   - 1.2 [Équipe projet](#12-équipe-projet)
2. [Analyse métier et principes comptables](#2-analyse-métier-et-principes-comptables)
   - 2.1 [Principes critiques de vigilance financière](#21-principes-critiques-de-vigilance-financière) *(Jenny Maeva - SFB-9)*
   - 2.2 [Grille de décision feu tricolore](#22-grille-de-décision-feu-tricolore) *(Comptables - SFB-10)*
   - 2.3 [Cas d'usage métier](#23-cas-dusage-métier) *(Comptables - SFB-11,12,13)*
3. [Spécifications fonctionnelles](#3-spécifications-fonctionnelles)
   - 3.1 [Inputs minimum MVP](#31-inputs-minimum-mvp) *(Comptables - SFB-14)*
   - 3.2 [Identification des acteurs et rôles](#32-identification-des-acteurs-et-rôles) *(Wandah - SFB-32)*
   - 3.3 [Diagramme de cas d'utilisation](#33-diagramme-de-cas-dutilisation) *(Mickbron - SFB-30)*
   - 3.4 [Cas d'utilisation détaillés - Dirigeant](#34-cas-dutilisation-détaillés---dirigeant) *(Steve Monthe - SFB-35)*
4. [Modélisation UML](#4-modélisation-uml)
   - 4.1 [Diagrammes d'activité](#41-diagrammes-dactivité)
     - 4.1.1 [Flux analyse prélèvement](#411-flux-analyse-prélèvement) *(Franck Njampou - SFB-38)*
     - 4.1.2 [Calculs comptables](#412-calculs-comptables) *(Mlle Nganang - SFB-37)*
   - 4.2 [Diagrammes de séquence](#42-diagrammes-de-séquence)
     - 4.2.1 [Authentification (Login & Register)](#421-authentification-login--register) *(Steven Talla - SFB-39)*
     - 4.2.2 [CRUD Profil Entreprise](#422-crud-profil-entreprise) *(Wilfred Tiwa - SFB-40)*
     - 4.2.3 [Analyse prélèvement](#423-analyse-prélèvement) *(Ndimou - SFB-41)*
   - 4.3 [Diagrammes d'états](#43-diagrammes-détats)
     - 4.3.1 [Cycle de vie Entreprise](#431-cycle-de-vie-entreprise) *(Merveille Guekep - SFB-42)*
     - 4.3.2 [Statut Prélèvement](#432-statut-prélèvement) *(Fongang Fotsing - SFB-43)*
   - 4.4 [Diagramme de classes](#44-diagramme-de-classes) *(Yvan Feuba - SFB-44)*
   - 4.5 [Diagramme ERD - Base de données](#45-diagramme-erd---base-de-données) *(À assigner - SFB-45)*
   - 4.6 [Diagramme de composants - Architecture](#46-diagramme-de-composants---architecture) *(À assigner - SFB-46)*
   - 4.7 [Diagramme de packages](#47-diagramme-de-packages) *(Wilfred Tiwa - SFB-49)*
5. [Conclusion](#5-conclusion)

---

## 1. Introduction

### 1.1 Objectif du document

Ce document constitue l'analyse fonctionnelle complète de l'application **SaveFunds**, réalisée durant le **Sprint 1** (16/02/2026 - 01/03/2026) par l'équipe de stagiaires Profacile SRL.

SaveFunds est une application web permettant aux dirigeants de PME belges de prendre des décisions éclairées sur leurs prélèvements personnels en analysant leur situation financière en temps réel.

**Problématique** : Les dirigeants de PME belges effectuent souvent des prélèvements personnels sans vision claire de leur impact sur la trésorerie, avec un risque de faillite.

**Solution** : SaveFunds analyse en temps réel la santé financière de l'entreprise et recommande (feu tricolore) si un prélèvement est sûr, risqué, ou dangereux.

### 1.2 Équipe projet

**Chef de projet** : Christian Sandjong Motio

**Équipe stagiaires** :
- **Comptabilité** : Jenny Maeva
- **Développement** : Steven Talla, Wilfred Tiwa, Yvan Feuba, Merveille Guekep, Fongang Fotsing, 
Franck Njampou, Mlle Nganang, Ndimou, Steve Monthe, Mickbron, Wandah, [Autres]

---

## 2. Analyse métier et principes comptables

### 2.1 Principes critiques de vigilance financière

**🎯 Ticket** : SFB-9  
**👤 Responsable** : Jenny Maeva (Comptabilité)  
**📦 Documentation des 7 principes comptables critiques** : 

---

**7 principes critiques** de vigilance financière pour PME belges :

1. **Compte courant dirigeant**
2. **Trésorerie minimum**
3. **Avantages toute nature (ATN)**
4. **Distinction prélèvement vs rémunération**
5. **Timing clôture fiscale**
6. **Ratio CA/Charges**
7. **Seuil santé financière**

Pour chaque principe, inclure :
- Description détaillée
- Implications fiscales/légales belges
- Seuils recommandés
- Exemples concrets

**Référence** : [Principes critiques comptables](docs/SFB-9-Principes-Critiques.md)

---

### 2.2 Grille de décision feu tricolore

**🎯 Ticket** : SFB-10  
**👤 Responsable** : Comptables  
**📦 Livrable** : Matrice de décision automatisée

---

 **grille de décision feu tricolore** :

#### Critères évalués

| # | Critère | Poids | 🟢 Vert | 🟡 Orange | 🔴 Rouge |
|---|---------|-------|---------|-----------|----------|
| 1 | Trésorerie disponible | 30% | > 4 mois charges | 2-4 mois | < 2 mois |
| 2 | Compte courant dirigeant | 25% | Créditeur | Débiteur < 3 mois | Débiteur ≥ 3 mois |
| 3 | Ratio CA/Charges | 20% | > 150% | 120-150% | < 120% |
| 4 | ... | ... | ... | ... | ... |

**la matrice avec TOUS les critères**

#### Règles de décision

- Score ≥ 80% → 🟢 **VERT** : Prélèvement autorisé
- Score 50-79% → 🟡 **ORANGE** : Vigilance requise
- Score < 50% → 🔴 **ROUGE** : Prélèvement déconseillé

**Référence** :  [grille de décision "Feu tricolore"](docs/SFB-10-Grille-Tricolore.md)

---

### 2.3 Cas d'usage métier

**🎯 Tickets** : SFB-11, SFB-12, SFB-13  
**👤 Responsable** : Comptables  
**📦 Livrable** : 3 cas d'usage détaillés (A, B, C)

---
 **3 cas réels** d'entreprises belges :

#### 🟢 Cas A : Le consultant prudent

**Profil** : [À compléter]
**Situation financière** : [À compléter]
**Demande de prélèvement** : [À compléter]
**Analyse SaveFunds** : [À compléter]
**Décision** : 🟢 VERT

---

#### 🟡 Cas B : La commerçante au CA irrégulier

**Profil** : [À compléter]
**Situation financière** : [À compléter - avec saisonnalité]
**Demande de prélèvement** : [À compléter]
**Analyse SaveFunds** : [À compléter]
**Décision** : 🟡 ORANGE

---

#### 🔴 Cas C : Le dirigeant en difficulté

**Profil** : [À compléter]
**Situation financière** : [À compléter - signaux d'alerte]
**Demande de prélèvement** : [À compléter]
**Analyse SaveFunds** : [À compléter]
**Décision** : 🔴 ROUGE

**Référence** : Fichier source `docs/CAS-USAGE-ABC-Analyses.md`

---

## 3. Spécifications fonctionnelles

### 3.1 Inputs minimum MVP

**🎯 Ticket** : SFB-14  
**👤 Responsable** : Comptables  
**📦 Livrable** : Liste des données d'entrée minimum

---

Documenter tous les **inputs nécessaires** pour le MVP :

#### 🏢 Profil Entreprise

| Champ | Type | Format | Exemple | Validation |
|-------|------|--------|---------|------------|
| Raison sociale | String | Texte | "Profacile SRL" | Requis |
| Numéro entreprise | String | BE 0XXX.XXX.XXX | "BE 0123.456.789" | Format BE |
| [À compléter] | ... | ... | ... | ... |

#### 💰 Situation Financière

| Champ | Type | Format | Exemple | Validation |
|-------|------|--------|---------|------------|
| CA mensuel | BigDecimal | €, 2 déc | 25000.00 | > 0 |
| Charges mensuelles | BigDecimal | €, 2 déc | 18000.00 | > 0 |
| [À compléter] | ... | ... | ... | ... |

#### 📝 Demande Prélèvement

[À compléter]

**Référence** : Fichier source `docs/SFB-14-Inputs-MVP.md`

---

### 3.2 Identification des acteurs et rôles

**🎯 Ticket** : SFB-32  
**👤 Responsable** : Wandah  
**📦 Livrable** : Liste des acteurs et leurs rôles

---

SaveFunds MVP implique **3 acteurs principaux** et **1 acteur secondaire** (système).

---

#### Acteur 1 : Dirigeant (Acteur principal)

**Description** : 

Dirigeant de PME belge (SPRL, SA, SNC, etc.) qui effectue des prélèvements personnels depuis son entreprise et souhaite s'assurer de ne pas mettre en danger sa trésorerie.

**Profil type** :
- Âge : 30-60 ans
- Entreprise : PME belge 1-50 employés
- CA annuel : 100K€ - 5M€
- Secteurs : Conseil, Commerce, Services, Artisanat
- Niveau informatique : Basique à intermédiaire
- Besoin : Sécurité financière, éviter la faillite

**Rôle** : Utilisateur principal de l'application

**Responsabilités** :

1. **Gestion de compte** :
   - Créer un compte SaveFunds (email + mot de passe)
   - Se connecter / Se déconnecter
   - Vérifier son email
   - Gérer son profil utilisateur
   - Modifier son mot de passe

2. **Gestion profil entreprise** :
   - Créer le profil de son entreprise (raison sociale, numéro BE, secteur)
   - Mettre à jour les données financières (CA, charges, trésorerie)
   - Consulter l'évolution de sa santé financière
   - Visualiser son statut (Active, En vigilance, En alerte)

3. **Analyses de prélèvement** :
   - Saisir une demande de prélèvement (montant, nature, date)
   - Lancer l'analyse automatique
   - Consulter le résultat (VERT/ORANGE/ROUGE)
   - Lire les recommandations personnalisées
   - Modifier et ré-analyser si nécessaire
   - Confirmer ou annuler le prélèvement
   - Consulter l'historique de toutes ses analyses

4. **Monitoring** :
   - Recevoir des alertes email (passage En vigilance ou En alerte)
   - Consulter son tableau de bord
   - Visualiser l'évolution de ses indicateurs
   - Télécharger des rapports (PDF)

**Permissions** :

- ✅ CRUD complet sur SON entreprise uniquement
- ✅ CRUD complet sur SES analyses de prélèvement
- ✅ Lecture de SES données uniquement
- ❌ Aucun accès aux données d'autres dirigeants
- ❌ Aucun accès aux fonctions d'administration

**Contraintes** :
- Un dirigeant ne peut gérer qu'UNE SEULE entreprise dans le MVP
- Post-MVP : Pourra gérer plusieurs entreprises (holding)

---

#### Acteur 2 : Administrateur SaveFunds (Acteur principal - post-MVP)

**Description** :

Employé de Profacile SRL responsable de la supervision de la plateforme SaveFunds. Gère les utilisateurs, surveille l'activité, génère des statistiques.

**Profil type** :
- Employé Profacile SRL
- Accès privilégié
- Connaissances techniques et métier

**Rôle** : Supervision et administration de la plateforme

**Responsabilités** :

1. **Gestion utilisateurs** :
   - Consulter la liste de tous les utilisateurs
   - Activer/Désactiver un compte
   - Réinitialiser mot de passe utilisateur
   - Supprimer un compte (RGPD)

2. **Monitoring global** :
   - Consulter statistiques globales (nombre utilisateurs, analyses, etc.)
   - Visualiser répartition VERT/ORANGE/ROUGE
   - Identifier entreprises en alerte critique
   - Générer rapports d'activité

3. **Support** :
   - Consulter logs d'erreurs
   - Aider un dirigeant bloqué
   - Corriger données erronées (validation)

4. **Configuration** :
   - Ajuster seuils de la grille tricolore (admin avancé)
   - Gérer paramètres système
   - Activer/Désactiver fonctionnalités

**Permissions** :

- ✅ Lecture de TOUTES les données (entreprises, analyses)
- ✅ Modification utilisateurs (activer/désactiver)
- ✅ Accès logs et statistiques
- ✅ Configuration système
- ❌ Modification directe des données métier des entreprises (intégrité)

**Note MVP** : Acteur non implémenté dans le MVP. Sera ajouté en Sprint 10-11.

---

#### Acteur 3 : Expert-comptable (Acteur optionnel - post-MVP)

**Description** :

Expert-comptable du dirigeant qui peut consulter (en lecture seule) les analyses de son client pour mieux le conseiller.

**Rôle** : Consultant externe

**Responsabilités** :
- Consulter profil entreprise de ses clients
- Consulter historique analyses
- Générer rapports pour conseils
- Recevoir alertes automatiques (entreprises clientes en alerte)

**Permissions** :
- ✅ Lecture uniquement sur entreprises dont il est le comptable
- ❌ Aucune modification
- ❌ Aucune création d'analyses (seul le dirigeant peut)

**Note MVP** : Acteur non implémenté dans le MVP. Version 2.

---

#### Acteur 4 : Système SaveFunds (Acteur secondaire automatisé)

**Description** :

Le système backend SaveFunds lui-même, qui effectue des traitements automatiques sans intervention humaine.

**Rôle** : Automatisation et calculs

**Responsabilités automatiques** :

1. **Calculs temps réel** :
   - Calculer indicateurs financiers à la demande
   - Appliquer grille tricolore
   - Générer score global pondéré
   - Déterminer couleur (VERT/ORANGE/ROUGE)
   - Calculer délai remboursement

2. **Monitoring quotidien** (Job CRON 2h du matin) :
   - Évaluer santé financière de toutes les entreprises actives
   - Changer statut entreprise si dégradation/amélioration
   - Créer historique changements d'état
   - Envoyer notifications email automatiques

3. **Notifications** :
   - Email de bienvenue (inscription)
   - Email de vérification
   - Alertes changement statut entreprise (vigilance, alerte)
   - Alertes analyse ROUGE
   - Rappels (prélèvement confirmé à date future)

4. **Sécurité** :
   - Génération tokens JWT
   - Hashage mots de passe (BCrypt)
   - Validation données entrantes
   - Logs d'audit

5. **Archivage** :
   - Archivage automatique entreprises inactives >2 ans
   - Purge données après 7 ans (obligation légale)

**Déclencheurs** :
- Appel API (temps réel)
- Job CRON (quotidien 2h)
- Événements métier (création compte, analyse, etc.)

---

### Matrice des permissions

| Action | Dirigeant | Admin | Comptable | Système |
|--------|-----------|-------|-----------|---------|
| **Créer compte** | ✅ | ✅ | ❌ | ❌ |
| **Créer entreprise** | ✅ (la sienne) | ❌ | ❌ | ❌ |
| **Modifier entreprise** | ✅ (la sienne) | ❌ | ❌ | ❌ |
| **Créer analyse** | ✅ (la sienne) | ❌ | ❌ | ❌ |
| **Lire toutes analyses** | ❌ | ✅ | ✅ (clients) | ✅ |
| **Calculer indicateurs** | ❌ | ❌ | ❌ | ✅ |
| **Changer statut entreprise** | ❌ | ❌ | ❌ | ✅ |
| **Envoyer notifications** | ❌ | ❌ | ❌ | ✅ |
| **Consulter logs** | ❌ | ✅ | ❌ | ✅ |
| **Modifier config système** | ❌ | ✅ | ❌ | ❌ |

---

### Diagramme de contexte (acteurs)

```
                    ┌──────────────┐
                    │   Dirigeant  │
                    │  (Principal) │
                    └──────┬───────┘
                           │
                           │ Utilise
                           ▼
        ┌──────────────────────────────────────┐
        │                                      │
        │        SaveFunds Application         │
        │                                      │
        │  ┌────────────────────────────────┐  │
        │  │  Système (Acteur secondaire)  │  │
        │  │  - Calculs automatiques       │  │
        │  │  - Notifications              │  │
        │  │  - Monitoring quotidien       │  │
        │  └────────────────────────────────┘  │
        │                                      │
        └──────────────────────────────────────┘
                           ▲
                           │
                           │ Administre (post-MVP)
                           │
                    ┌──────┴────────┐
                    │ Administrateur │
                    │  (Post-MVP)    │
                    └────────────────┘
```

---

**MVP = Scope minimal** : 
- Acteur implémenté : **Dirigeant** + **Système**
- Post-MVP : Administrateur, Expert-comptable

---

### 3.3 Diagramme de cas d'utilisation

**🎯 Ticket** : SFB-30  
**👤 Responsable** : Mickbron  
**📦 Livrable** : Diagramme Use Case complet

---

**[ 📝 À COMPLÉTER PAR MICKBRON ]**

Créer le **diagramme de cas d'utilisation** (Use Case Diagram) :

#### Description

[À compléter : 2-3 paragraphes expliquant le diagramme]

#### Diagramme

![Diagramme Use Case - SaveFunds](images/use-case-diagram.pdf)

#### Cas d'utilisation identifiés

1. **S'inscrire**
   - Acteur : Dirigeant
   - Description courte : [À compléter]

2. **Se connecter**
   - Acteur : Dirigeant
   - Description courte : [À compléter]

3. **Créer profil entreprise**
   - Acteur : Dirigeant
   - Description courte : [À compléter]

4. **Analyser prélèvement**
   - Acteur : Dirigeant
   - Description courte : [À compléter]

5. **[ À compléter : Lister TOUS les use cases ]**

---

### 3.4 Cas d'utilisation détaillés - Dirigeant

**🎯 Ticket** : SFB-35  
**👤 Responsable** : Steve Monthe  
**📦 Livrable** : Fiches détaillées des cas d'utilisation

---

**STEVE MONTHE**

**5-7 cas d'utilisation principaux** :

#### UC-01 : S'inscrire

**Acteur principal** : Dirigeant  
**Préconditions** : Aucune  
**Postconditions** : Compte créé, email de vérification envoyé

**Scénario nominal** :
1. Le dirigeant clique sur "Créer un compte"
2. Le système affiche le formulaire d'inscription
3. Le dirigeant saisit : nom, prénom, email, mot de passe
4. Le système valide le format email et la force du mot de passe
5. Le système vérifie que l'email n'existe pas déjà
6. Le système crée le compte avec statut "non vérifié"
7. Le système envoie un email de vérification
8. Le système affiche un message de confirmation

**Scénarios alternatifs** :
- **4a** : Format email invalide → Message d'erreur, retour étape 3
- **4b** : Mot de passe trop faible → Message d'erreur, retour étape 3
- **5a** : Email déjà existant → Message d'erreur, proposition de connexion

**Règles métier** :
- RG-01 : Email doit être unique
- RG-02 : Mot de passe minimum 8 caractères, 1 majuscule, 1 chiffre
- [À compléter]

---

#### UC-02 : Se connecter

**[ À compléter de la même manière ]**

---

#### UC-03 : Créer profil entreprise

**[ À compléter de la même manière ]**

---

#### UC-04 : Analyser prélèvement

**[ À compléter de la même manière ]**

---

**[ Documenter 3-4 autres UC principaux ]**

---

## 4. Modélisation UML

### 4.1 Diagrammes d'activité

#### 4.1.1 Flux analyse prélèvement

**🎯 Ticket** : SFB-38  
**👤 Responsable** : Franck Njampou  
**📦 Livrable** : Diagramme d'activité du flux complet

---

**[ 📝 À COMPLÉTER PAR FRANCK NJAMPOU ]**

Créer le **diagramme d'activité** représentant le flux complet d'une analyse de prélèvement :

##### Description

[À compléter : Expliquer le processus global]

##### Diagramme

![Diagramme d'activité - Flux analyse](images/activity-flux-analyse.png)

**Format** : PNG, minimum 1200px de large

##### Étapes principales

1. **Début** : Dirigeant demande une analyse
2. **Vérification données** : Système vérifie que le profil entreprise est complet
3. **Saisie montant** : Dirigeant saisit montant et nature du prélèvement
4. **[À compléter : Toutes les étapes du flow]**
5. **Décision finale** : VERT / ORANGE / ROUGE
6. **Fin**

##### Points de décision

- **Decision 1** : Profil entreprise complet ? Oui/Non
- **Decision 2** : [À compléter]
- **[À compléter]**

##### Swimlanes (couloirs)

- Dirigeant
- Système Frontend
- Système Backend
- [À compléter si nécessaire]

---

#### 4.1.2 Calculs comptables

**🎯 Ticket** : SFB-37  
**👤 Responsable** : Mlle Nganang  
**📦 Livrable** : Diagramme d'activité des calculs

---

**[ 📝 À COMPLÉTER PAR MLLE NGANANG ]**

Créer le **diagramme d'activité** détaillant les calculs comptables :

##### Description

[À compléter : Expliquer la logique de calcul]

##### Diagramme

![Diagramme d'activité - Calculs](images/activity-calculs.png)

**Format** : PNG, minimum 1200px de large

##### Calculs effectués

1. **Ratio CA/Charges** : `(CA / Charges) × 100`
2. **Trésorerie en mois** : `Trésorerie / Charges mensuelles`
3. **Score critère 1** : [À compléter]
4. **[À compléter : Tous les calculs]**
5. **Score global pondéré** : `Σ (Critère_i × Poids_i)`
6. **Décision finale** : Basée sur score global

##### Formules

```
Score global = (Critère1 × 30%) + (Critère2 × 25%) + ...

SI Score ≥ 80% ALORS Décision = VERT
SINON SI Score ≥ 50% ALORS Décision = ORANGE
SINON Décision = ROUGE
```

---

### 4.2 Diagrammes de séquence

#### 4.2.1 Authentification (Login & Register)

**🎯 Ticket** : SFB-39  
**👤 Responsable** : Steven Talla  
**📦 Livrable** : 2 diagrammes de séquence

---

**[ 📝 À COMPLÉTER PAR STEVEN TALLA ]**

Créer **2 diagrammes de séquence** pour l'authentification :

##### Diagramme 1 : Login

**Participants** :
- Dirigeant
- Frontend
- Backend (Controller → Service → Repository)
- Base de données

**Description** : [À compléter]

![Diagramme séquence - Login](images/sequence-login.png)

**Étapes** :
1. Dirigeant saisit email + mot de passe
2. Frontend → Backend : POST /api/auth/login
3. Backend vérifie credentials (BCrypt)
4. Backend génère JWT (expiration 24h)
5. Backend → Frontend : {token, user}
6. Frontend stocke token (sessionStorage)
7. Redirection Dashboard

**Messages HTTP** :
```
POST /api/auth/login
Body: { email: "...", password: "..." }
Response 200: { token: "jwt...", user: {...} }
Response 401: { error: "Invalid credentials" }
```

---

##### Diagramme 2 : Register

**Participants** :
- Dirigeant
- Frontend
- Backend (Controller → Service → Repository)
- Base de données
- EmailService

**Description** : [À compléter]

![Diagramme séquence - Register](images/sequence-register.png)

**Étapes** :
1. [À compléter]
2. [À compléter - inclure envoi email de vérification]

**Messages HTTP** :
```
POST /api/auth/register
Body: { nom: "...", email: "...", password: "..." }
Response 201: { message: "Account created" }
Response 409: { error: "Email already exists" }
```

**⚠️ CORRECTIONS À APPLIQUER** :
- Ajouter Frontend comme 4ème participant (OBLIGATOIRE)
- Utiliser flèches pointillées pour les retours
- Inclure les 2 diagrammes (Login ET Register)

**Référence** : `corrections/SFB-39-corrections.md`

---

#### 4.2.2 CRUD Profil Entreprise

**🎯 Ticket** : SFB-40  
**👤 Responsable** : Wilfred Tiwa  
**📦 Livrable** : 3 diagrammes de séquence (CREATE, UPDATE, READ)

---

**[ 📝 À COMPLÉTER PAR WILFRED TIWA ]**

Créer **3 diagrammes de séquence** pour le CRUD Profil Entreprise :

##### Diagramme 1 : CREATE - Créer profil entreprise

**Participants** :
- Dirigeant
- Frontend
- Backend (Controller → Service → Repository)
- Base de données

**Description** : [À compléter]

![Diagramme séquence - Create Entreprise](images/sequence-create-entreprise.png)

**Étapes** :
1. Dirigeant remplit formulaire profil entreprise
2. Frontend valide les données
3. Frontend → Backend : POST /api/entreprises
4. Backend vérifie ownership (userId)
5. Backend crée l'entreprise
6. Backend → Frontend : {entreprise}
7. Frontend affiche confirmation

**Messages HTTP** :
```
POST /api/entreprises
Headers: Authorization: Bearer {token}
Body: {
  raisonSociale: "...",
  numeroEntreprise: "BE ...",
  caMensuel: 25000.00,
  ...
}
Response 201: { id: 1, ... }
Response 403: { error: "Forbidden" }
```

---

##### Diagramme 2 : UPDATE - Modifier profil

**[ À compléter de la même manière ]**

```
PUT /api/entreprises/{id}
```

---

##### Diagramme 3 : READ - Consulter profil

**[ À compléter de la même manière ]**

```
GET /api/entreprises/{id}
```

**⚠️ CORRECTIONS À APPLIQUER** :
- Corriger l'entité : ENTREPRISE (pas CompteCourant)
- Ajouter Frontend comme 4ème participant
- Vérifier ownership (userId) dans tous les diagrammes

**Référence** : `corrections/SFB-40-corrections.md`

---

#### 4.2.3 Analyse prélèvement

**🎯 Ticket** : SFB-41  
**👤 Responsable** : Ndimou  
**📦 Livrable** : Diagramme de séquence analyse

---

**[ 📝 À COMPLÉTER PAR NDIMOU ]**

Créer le **diagramme de séquence** pour l'analyse d'un prélèvement :

##### Description

[À compléter : Processus complet d'analyse]

##### Diagramme

![Diagramme séquence - Analyse](images/sequence-analyse.png)

##### Participants

- Dirigeant
- Frontend
- Backend (Controller → Service → Repository)
- Base de données
- AnalyseService (service métier)

##### Étapes principales

1. Dirigeant saisit montant + nature prélèvement
2. Frontend → Backend : POST /api/analyses
3. Backend récupère profil entreprise et situation financière
4. Backend calcule les critères (via AnalyseService)
5. Backend applique la grille tricolore
6. Backend détermine décision (VERT/ORANGE/ROUGE)
7. Backend calcule délai remboursement
8. Backend sauvegarde résultat
9. Backend → Frontend : {resultat: "VERT", score: 85%, ...}
10. Frontend affiche résultat avec recommandations

##### Messages HTTP

```
POST /api/analyses
Headers: Authorization: Bearer {token}
Body: {
  entrepriseId: 1,
  montant: 3000.00,
  nature: "PRELEVEMENT_PERSONNEL"
}
Response 200: {
  decision: "VERT",
  scoreGlobal: 85.5,
  details: {...},
  recommandation: "..."
}
```

---

### 4.3 Diagrammes d'états

#### 4.3.1 Cycle de vie Entreprise

**🎯 Ticket** : SFB-42  
**👤 Responsable** : Merveille Guekep  
**📦 Livrable** : Diagramme d'états cycle vie

---

##### Description

Ce diagramme représente les différents états par lesquels passe une entreprise dans SaveFunds, en fonction de sa santé financière. Le système évalue automatiquement chaque nuit (2h du matin) les indicateurs financiers de chaque entreprise et ajuste son état en conséquence.

L'objectif est de **surveiller en continu** la santé financière et **alerter préventivement** le dirigeant avant qu'il ne soit trop tard.

##### Diagramme

![Diagramme états - Cycle vie Entreprise](images/state-entreprise.png)

**Format** : PNG, 127 KB, 1400px × 1800px

---

##### États détaillés

**1. ● État initial**

Point de départ du cycle de vie.

---

**2. En création**

- Profil entreprise incomplet
- Utilisateur en cours de saisie des données
- Ne peut pas encore faire d'analyses de prélèvement
- **Données manquantes** : Raison sociale, numéro entreprise, CA mensuel, charges, trésorerie

**Sortie** : Lorsque toutes les données obligatoires sont saisies et validées → **Active**

---

**3. Active ✓ (VERT)**

- Profil complet et validé
- Situation financière saine
- Tous les indicateurs au vert
- Peut demander des analyses de prélèvement

**Conditions** :
- Trésorerie ≥ 4 mois de charges
- Ratio CA/Charges ≥ 150%
- Compte courant créditeur ou débiteur < 3 mois

**Comportement** :
- Calcul automatique quotidien (job CRON 2h du matin)
- Analyses prélèvement disponibles
- Résultats généralement VERTS

**Sorties** :
- → **En vigilance** : Si indicateurs se dégradent
- → **Archivée** : Si fermeture demandée par dirigeant

---

**4. En vigilance ⚠ (ORANGE)**

- Indicateurs financiers dégradés
- Surveillance renforcée activée
- Prélèvements souvent évalués ORANGE
- Alertes email automatiques

**Déclencheurs** (au moins 1) :
- Trésorerie entre 2-4 mois
- Ratio CA/Charges entre 120-150%
- Compte courant débiteur entre 1-3 mois
- Baisse significative CA (> 20% sur 3 mois)

**Actions système** :
- Email d'alerte au dirigeant
- Recommandations personnalisées
- Suivi quotidien automatique
- Limitation suggérée des prélèvements

**Sorties** :
- → **Active** : Si amélioration des indicateurs
- → **En alerte** : Si dégradation continue
- → **Archivée** : Si fermeture demandée

---

**5. En alerte ⛔ (ROUGE)**

- Situation financière critique
- Risque élevé de faillite
- Prélèvements généralement ROUGES
- Accompagnement urgent requis

**Déclencheurs** (au moins 1) :
- Trésorerie < 2 mois
- Ratio CA/Charges < 120%
- Compte courant débiteur ≥ 3 mois
- Pertes cumulées > 50% du capital

**Actions système** :
- Email + notification urgente
- Blocage préventif des prélèvements
- Suggestion consultation expert comptable
- Contact téléphonique (si service premium - post-MVP)

**Sorties** :
- → **En vigilance** : Si redressement partiel
- → **Active** : Si rétablissement complet (rare, ~5% des cas)
- → **Archivée** : Si faillite déclarée ou fermeture

---

**6. Archivée 📦**

- Entreprise fermée ou liquidée
- Compte inactif
- Données conservées pour historique
- Plus d'analyses possibles

**Causes** :
- Fermeture volontaire (demande dirigeant)
- Faillite déclarée
- Liquidation judiciaire
- Inactivité prolongée (> 2 ans sans connexion)

**Conservation** :
- Historique complet des analyses
- Évolution des indicateurs
- Toutes les décisions prises
- **Durée** : 7 ans (obligation légale comptable belge - art. 6 Loi comptabilité)

**Sortie** : → **●** État final (destruction définitive après 7 ans)

---

**7. ● État final**

Fin du cycle de vie après les 7 ans légaux.

---

##### Transitions complètes

| De | Vers | Déclencheur | Condition | Automatique |
|----|------|-------------|-----------|-------------|
| **Initial** | En création | Création compte | Inscription validée | ✅ |
| **En création** | Active | Profil complété | Toutes données saisies | ✅ |
| **Active** | En vigilance | Indicateurs dégradés | 1+ indicateur orange | ✅ Quotidien |
| **En vigilance** | Active | Amélioration | Tous indicateurs verts | ✅ Quotidien |
| **En vigilance** | En alerte | Situation critique | 1+ indicateur rouge | ✅ Quotidien |
| **En alerte** | En vigilance | Redressement | Indicateurs > orange | ✅ Quotidien |
| **En alerte** | Active | Rétablissement | Tous indicateurs verts | ✅ Quotidien |
| **Active** | Archivée | Fermeture | Demande utilisateur | ❌ Manuel |
| **En vigilance** | Archivée | Fermeture | Demande utilisateur | ❌ Manuel |
| **En alerte** | Archivée | Faillite/Fermeture | Faillite OU Demande | ❌ Manuel |
| **Archivée** | Final | Expiration | 7 ans écoulés | ✅ |

---

##### Règles métier - Calcul automatique

**Job CRON quotidien** : Chaque nuit à 2h00

```pseudo
POUR chaque Entreprise (statut IN ['ACTIVE', 'EN_VIGILANCE', 'EN_ALERTE']) :
  
  1. Récupérer dernière SituationFinanciere
  
  2. Calculer indicateurs :
     ratioCACharges = (CA / Charges) × 100
     tresorerieEnMois = Trésorerie / Charges
     statusCC = (Débiteur ? duréeEnMois : 0)
  
  3. Évaluer selon grille tricolore :
     compterIndicateursVERT()
     compterIndicateursORANGE()
     compterIndicateursROUGE()
  
  4. Déterminer nouvel état :
     SI (1+ indicateur ROUGE) :
       nouvelEtat = EN_ALERTE
     SINON SI (1+ indicateur ORANGE) :
       nouvelEtat = EN_VIGILANCE
     SINON :
       nouvelEtat = ACTIVE
  
  5. SI (nouvelEtat ≠ statutActuel) :
     entreprise.changerStatut(nouvelEtat)
     creerHistoriqueChangement(entreprise, statutActuel, nouvelEtat)
     envoyerNotificationEmail(entreprise, nouvelEtat)
     loggerEvenement(entreprise, nouvelEtat)
```

---

##### Statistiques attendues

**Distribution typique** (sur 1000 entreprises PME belges) :
- **Active** : 750 (75%)
- **En vigilance** : 200 (20%)
- **En alerte** : 40 (4%)
- **Archivée** : 10 (1%)

**Transitions moyennes par mois** :
- Active ↔ En vigilance : 100-150 transitions
- En vigilance ↔ En alerte : 20-30 transitions
- Vers Archivée : 5-10 entreprises

---

##### Notifications

| Changement d'état | Email | Notif app | SMS (premium) |
|-------------------|-------|-----------|---------------|
| Active → En vigilance | ✅ | ✅ | ❌ |
| En vigilance → En alerte | ✅ | ✅ | ✅ |
| En alerte → En vigilance | ✅ | ✅ | ❌ |
| Vigilance/Alerte → Active | ✅ | ✅ | ❌ |

---

**Note** : L'état "Suspended" (compte suspendu pour problème de paiement) mentionné dans l'énoncé initial a été supprimé car le MVP est gratuit. Cet état sera ajouté en V2 lors de la commercialisation.

---

#### 4.3.2 Statut Prélèvement

**🎯 Ticket** : SFB-43  
**👤 Responsable** : Fongang Fotsing  
**📦 Livrable** : Diagramme d'états statut prélèvement

---

##### Description

Ce diagramme représente les différents états par lesquels passe une **demande de prélèvement** depuis sa création jusqu'à sa finalisation (confirmée ou annulée).

Le flux illustre le processus complet d'analyse : saisie des données → analyse automatique → résultat (VERT/ORANGE/ROUGE) → décision finale du dirigeant.

**Point clé** : Une analyse peut être modifiée et ré-analysée plusieurs fois avant confirmation ou annulation définitive.

##### Diagramme

![Diagramme états - Statut Prélèvement](images/state-prelevement.png)

**Format** : PNG, minimum 1200px de large

---

##### États détaillés

**1. ● État initial**

Point de départ : Le dirigeant décide de faire une demande de prélèvement.

---

**2. En saisie**

- Dirigeant remplit le formulaire de prélèvement
- Montant, nature, justification
- Peut sauvegarder en brouillon
- Peut modifier à tout moment

**Données saisies** :
- Montant du prélèvement (BigDecimal)
- Nature (enum : PRELEVEMENT_PERSONNEL, REMBOURSEMENT_AVANCE, DIVIDENDE, etc.)
- Date souhaitée
- Justification (optionnel)

**Actions disponibles** :
- Bouton "Analyser" → Déclenche l'analyse
- Bouton "Annuler" → Supprime le brouillon

**Sortie** : Clic sur "Analyser" → **En cours d'analyse**

---

**3. En cours d'analyse** 

- Calculs automatiques en cours
- Spinner/loader affiché à l'utilisateur
- Application de la grille tricolore
- Génération des recommandations

**Durée** : ~2-5 secondes

**Calculs effectués** :
1. Récupération situation financière entreprise
2. Calcul ratio CA/Charges
3. Calcul trésorerie en mois
4. Évaluation compte courant
5. Application grille tricolore pondérée
6. Calcul score global
7. Détermination couleur (VERT/ORANGE/ROUGE)
8. Calcul délai remboursement suggéré
9. Génération recommandations personnalisées

**Sorties** :
- → **Analysé - VERT** : Si score ≥ 80%
- → **Analysé - ORANGE** : Si score 50-79%
- → **Analysé - ROUGE** : Si score < 50%

---

**4. Analysé - VERT ✅**

- Résultat positif
- Prélèvement sûr, aucun risque identifié
- Score global ≥ 80%

**Affichage** :
- Badge VERT avec icône ✓
- Message : "Prélèvement sûr - Aucun risque détecté"
- Score détaillé par critère
- Délai remboursement suggéré
- Recommandation : "Vous pouvez effectuer ce prélèvement en toute sécurité"

**Actions disponibles** :
- **Confirmer** → Valide définitivement le prélèvement → **Confirmé ●**
- **Modifier** → Retour en saisie pour ajuster le montant → **En saisie**
- **Annuler** → Abandonne la demande → **Annulé ●**

---

**5. Analysé - ORANGE ⚠️**

- Résultat vigilance
- Prélèvement risqué, attention requise
- Score global 50-79%

**Affichage** :
- Badge ORANGE avec icône ⚠
- Message : "Vigilance requise - Risque modéré"
- Score détaillé + critères en alerte
- Délai remboursement impératif
- Recommandation : "Privilégiez un montant plus faible ou attendez une amélioration de trésorerie"
- Suggestions alternatives (montant réduit, date différée)

**Actions disponibles** :
- **Confirmer** → Prélèvement malgré risque (avec confirmation supplémentaire) → **Confirmé ●**
- **Modifier** → Ajuster le montant à la baisse → **En saisie**
- **Annuler** → Sage décision → **Annulé ●**

---

**6. Analysé - ROUGE 🔴**

- Résultat alerte
- Prélèvement dangereux, risque élevé
- Score global < 50%

**Affichage** :
- Badge ROUGE avec icône 🚨
- Message : "DANGER - Risque élevé de faillite"
- Score détaillé + tous critères en rouge
- Impossibilité de remboursement calculée
- Avertissement : "Ce prélèvement pourrait mettre en danger votre entreprise"
- Recommandation forte : "Nous vous déconseillons fortement ce prélèvement"
- Suggestion : Contact avec expert comptable

**Actions disponibles** :
- **Confirmer** → TRÈS fortement déconseillé (double confirmation requise) → **Confirmé ●**
- **Modifier** → Réduire drastiquement le montant → **En saisie**
- **Annuler** → Recommandé → **Annulé ●**

---

**7. Confirmé ● (État final)**

- Dirigeant a validé définitivement le prélèvement
- Enregistrement dans historique
- Email de confirmation envoyé
- Suivi activé (si date future)
- **Immuable** : Ne peut plus être modifié

**Post-traitement** :
- Ajout dans tableau de bord
- Notification comptable (si intégration)
- Export possible (PDF, email)
- Mise à jour stats entreprise

**Cercle noir plein ●** : État terminal

---

**8. Annulé ● (État final)**

- Dirigeant a abandonné la demande
- Aucun impact sur l'entreprise
- Conservé dans historique (avec statut "Annulé")

**Raisons possibles** :
- Résultat ROUGE dissuasif
- Changement de situation
- Montant jugé trop risqué
- Report à plus tard

**Cercle noir plein ●** : État terminal

---

**9. ● État final**

Fin du cycle de vie de la demande (après confirmation ou annulation).

---

##### Transitions complètes

| Transition | Déclencheur | Action utilisateur | Automatique |
|------------|-------------|-------------------|-------------|
| **Initial → En saisie** | Création demande | Clic "Nouvelle analyse" | ❌ |
| **En saisie → En cours d'analyse** | Lancement analyse | Clic "Analyser" | ❌ |
| **En cours d'analyse → Analysé VERT** | Résultat calcul | Score ≥ 80% | ✅ |
| **En cours d'analyse → Analysé ORANGE** | Résultat calcul | Score 50-79% | ✅ |
| **En cours d'analyse → Analysé ROUGE** | Résultat calcul | Score < 50% | ✅ |
| **Analysé VERT → Confirmé ●** | Validation | Clic "Confirmer" | ❌ |
| **Analysé ORANGE → Confirmé ●** | Validation | Clic "Confirmer" (+ double confirm) | ❌ |
| **Analysé ROUGE → Confirmé ●** | Validation | Clic "Confirmer" (+ TRIPLE confirm) | ❌ |
| **Analysé * → En saisie** | Modification | Clic "Modifier" | ❌ |
| **Analysé * → Annulé ●** | Abandon | Clic "Annuler" | ❌ |

**Note** : `Analysé *` signifie "n'importe quel état Analysé (VERT/ORANGE/ROUGE)"

---

##### Règles métier importantes

**1. État "En cours d'analyse" obligatoire**

⚠️ **Erreur fréquente** : Oublier cet état intermédiaire

Pourquoi il est essentiel :
- Feedback utilisateur (spinner)
- Traçabilité (logs des analyses)
- Gestion des erreurs techniques
- Timeouts possibles

**2. Modifications multiples autorisées**

Un dirigeant peut :
```
En saisie → Analyser → Analysé ORANGE → Modifier → En saisie
         → Analyser → Analysé VERT → Modifier → En saisie
         → Analyser → Analysé VERT → Confirmer ●
```

Historique complet conservé pour audit.

**3. Confirmations progressives selon couleur**

- **VERT** : 1 clic "Confirmer"
- **ORANGE** : 2 clics (popup : "Êtes-vous sûr malgré les risques ?")
- **ROUGE** : 3 clics (popup : "DANGER ! Confirmez que vous comprenez les risques")

**4. États finaux immuables**

Une fois **Confirmé** ou **Annulé** :
- Aucune modification possible
- Conservé dans historique
- Utilisé pour statistiques

Pour refaire : Créer une nouvelle demande.

---

##### Diagramme de séquence associé

Ce diagramme d'états est complémentaire au **diagramme de séquence SFB-41** (Analyse prélèvement) qui montre les interactions entre composants.

**Correspondance** :
- POST /api/analyses → Transition "En saisie → En cours d'analyse"
- Calcul backend → État "En cours d'analyse"
- Response 200 → Transition vers "Analysé VERT/ORANGE/ROUGE"
- PATCH /api/analyses/{id}/confirm → Transition vers "Confirmé"

---

##### Statistiques attendues

**Distribution des analyses** (sur 1000 analyses) :
- **VERT** : 450 (45%) → 90% confirmées
- **ORANGE** : 400 (40%) → 60% confirmées, 30% modifiées, 10% annulées
- **ROUGE** : 150 (15%) → 10% confirmées (malgré risque), 20% modifiées, 70% annulées

**Taux de modification** : 25% des analyses sont modifiées puis re-analysées.

---

##### Implémentation (Enum Java)

```java
package com.profacile.savefunds.model;

public enum StatutPrelevement {
    EN_SAISIE("En saisie", "Formulaire en cours de remplissage"),
    EN_COURS_ANALYSE("En cours d'analyse", "Calculs en cours"),
    ANALYSE_VERT("Analysé - VERT", "Prélèvement sûr"),
    ANALYSE_ORANGE("Analysé - ORANGE", "Vigilance requise"),
    ANALYSE_ROUGE("Analysé - ROUGE", "Risque élevé"),
    CONFIRME("Confirmé", "Prélèvement validé"),
    ANNULE("Annulé", "Demande abandonnée");
    
    private final String label;
    private final String description;
    
    StatutPrelevement(String label, String description) {
        this.label = label;
        this.description = description;
    }
    
    public boolean estFinal() {
        return this == CONFIRME || this == ANNULE;
    }
    
    public boolean estAnalyse() {
        return this == ANALYSE_VERT || this == ANALYSE_ORANGE || this == ANALYSE_ROUGE;
    }
}
```

---

**⚠️ Points clés pour Fongang** :
1. ✅ État "En cours d'analyse" OBLIGATOIRE (souvent oublié)
2. ✅ États finaux (Confirmé, Annulé) avec cercle noir ●
3. ✅ 3 états "Analysé" distincts (pas un seul)
4. ✅ Transitions bidirectionnelles "Analysé ↔ En saisie"
5. ✅ Cohérence avec grille tricolore (seuils 50%, 80%)

---

### 4.4 Diagramme de classes

**🎯 Ticket** : SFB-44  
**👤 Responsable** : Yvan Feuba  
**📦 Livrable** : Diagramme de classes complet

---

**[ 📝 À COMPLÉTER PAR YVAN FEUBA ]**

Créer le **diagramme de classes** du domain model SaveFunds :

##### Description

[À compléter : Architecture du modèle de domaine]

##### Diagramme

![Diagramme de classes](images/class-diagram.png)

**Format** : PNG, minimum 1500px de large

##### Classes principales

**1. User**

```java
@Entity
@Table(name = "users")
class User {
  // Attributs
  - id : Long
  - email : String
  - motDePasse : String  // ⚠️ Correction : motDePasse (pas motDePass)
  - nom : String
  - prenom : String
  - emailVerified : Boolean  // ✅ AJOUTER
  - role : Role
  - createdAt : LocalDateTime
  
  // Méthodes
  + register() : User
  + login() : String
  + verifyEmail() : void
}
```

---

**2. Entreprise**

```java
@Entity
@Table(name = "entreprises")
class Entreprise {
  // Attributs
  - id : Long
  - userId : Long
  - raisonSociale : String
  - numeroEntreprise : String
  - formeJuridique : FormeJuridique
  - secteurActivite : String
  - chiffreAffairesMensuel : BigDecimal  // ⚠️ Correction : chiffreAffairesMensuel (pas chiffreAffaireMensuel)
  - chargesMensuelles : BigDecimal
  - tresorerie : BigDecimal
  - soldeCompteCourant : BigDecimal
  - createdAt : LocalDateTime
  - updatedAt : LocalDateTime
  
  // Méthodes
  + create() : Entreprise
  + update() : Entreprise
  + delete() : void
  + calculerRatioCACharges() : BigDecimal
  + calculerTresorerieEnMois() : BigDecimal
}
```

---

**3. AnalysePrelevement**

```java
@Entity
@Table(name = "analyses_prelevement")
class AnalysePrelevement {
  // Attributs
  - id : Long
  - entrepriseId : Long
  - montant : BigDecimal  // ✅ BigDecimal (pas double)
  - nature : NaturePrelevement
  - dateDemande : LocalDateTime
  - statutAnalyse : StatutAnalyse  // ✅ AJOUTER cet enum
  
  // Méthodes
  + analyser() : ResultatAnalyse
  + confirmer() : void
  + annuler() : void
}
```

---

**4. ResultatAnalyse** ✅ **AJOUTER cette classe (Value Object)**

```java
class ResultatAnalyse {
  // Attributs
  - analyseId : Long
  - decision : Decision  // VERT, ORANGE, ROUGE
  - scoreGlobal : BigDecimal
  - delaiRemboursement : Integer  // en jours
  - recommandation : String
  - dateCalcul : LocalDateTime
  
  // Méthodes
  + genererRecommandation() : String
}
```

---

**5. SituationFinanciere** ✅ **AJOUTER cette classe**

```java
@Entity
@Table(name = "situations_financieres")
class SituationFinanciere {
  // Attributs
  - id : Long
  - entrepriseId : Long
  - date : LocalDate
  - chiffreAffaires : BigDecimal
  - charges : BigDecimal
  - tresorerie : BigDecimal
  - soldeCompteCourant : BigDecimal
  
  // Méthodes
  + enregistrer() : void
  + calculerRatio() : BigDecimal
}
```

---

**6. CritereEvaluation**

```java
class CritereEvaluation {
  // Attributs
  - id : Long
  - resultatAnalyseId : Long
  - nomCritere : String
  - valeur : BigDecimal
  - score : BigDecimal
  - poids : BigDecimal
  - seuil : String  // VERT, ORANGE, ROUGE
  
  // Méthodes
  + evaluer() : BigDecimal
}
```

---

##### Enums

**Role**

```java
enum Role {
  DIRIGEANT,
  ADMIN
}
```

---

**Decision**

```java
enum Decision {
  VERT,
  ORANGE,
  ROUGE
}
```

---

**NaturePrelevement**

```java
enum NaturePrelevement {
  PRELEVEMENT_PERSONNEL,
  REMBOURSEMENT_AVANCE,
  DIVIDENDE,
  REMUNERATION_SUPPLEMENTAIRE,
  AUTRE
}
```

---

**StatutAnalyse** ✅ **AJOUTER cet enum**

```java
enum StatutAnalyse {
  EN_SAISIE,
  EN_COURS_ANALYSE,
  ANALYSE_VERT,
  ANALYSE_ORANGE,
  ANALYSE_ROUGE,
  CONFIRME,
  ANNULE
}
```

---

**FormeJuridique**

```java
enum FormeJuridique {
  SPRL,
  SA,
  SNC,
  ASBL,
  AUTRE
}
```

---

##### Relations

```
User 1 ──────────────── 0..* Entreprise
Entreprise 1 ─────────── 0..* SituationFinanciere
Entreprise 1 ─────────── 0..* AnalysePrelevement
AnalysePrelevement 1 ─── 1 ResultatAnalyse
ResultatAnalyse 1 ────── 0..* CritereEvaluation
```

**[ À compléter : Détailler chaque relation avec cardinalités ]**

---

##### ⚠️ CORRECTIONS CRITIQUES

**Types de données** :
- ✅ `BigDecimal` pour TOUS les montants (pas `double`)
- ✅ `Long` pour les IDs (pas `int`)
- ✅ `LocalDateTime` pour dates avec heure
- ✅ `LocalDate` pour dates seules

**Fautes à corriger** :
- ~~motDePass~~ → `motDePasse`
- ~~chiffreAffaireMensuel~~ → `chiffreAffairesMensuel`

**Classes manquantes à ajouter** :
- ✅ `ResultatAnalyse` (Value Object)
- ✅ `SituationFinanciere`

**Enums manquants à ajouter** :
- ✅ `StatutAnalyse`

**Annotations JPA à mentionner** :
- `@Entity` sur toutes les entités
- `@Table(name = "...")
- `@Id` + `@GeneratedValue` sur les IDs
- `@ManyToOne` / `@OneToMany` sur les relations
- `@Column` avec contraintes

**❌ À NE PAS inclure** :
- JWT Token (stocké côté client, pas dans le domaine)
- PasswordResetToken (optionnel post-MVP)

**Référence** : `corrections/SFB-44-corrections.md`

---

### 4.5 Diagramme ERD - Base de données

**🎯 Ticket** : SFB-45  
**👤 Responsable** : À assigner  
**📦 Livrable** : Diagramme ERD complet

---

**[ 📝 À COMPLÉTER PAR [NOM] ]**

Créer le **diagramme ERD** (Entity-Relationship Diagram) de la base de données :

##### Description

[À compléter : Architecture base de données PostgreSQL]

##### Diagramme

![Diagramme ERD](images/erd.png)

**Format** : PNG, minimum 1500px de large

##### Tables principales

**users**

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGSERIAL | PRIMARY KEY |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| nom | VARCHAR(100) | NOT NULL |
| prenom | VARCHAR(100) | NOT NULL |
| email_verified | BOOLEAN | DEFAULT FALSE |
| role | VARCHAR(20) | NOT NULL |
| created_at | TIMESTAMP | DEFAULT NOW() |

---

**entreprises**

**[ À compléter ]**

---

**analyses_prelevement**

**[ À compléter ]**

---

**resultats_analyse**

**[ À compléter ]**

---

**[ Documenter TOUTES les tables ]**

##### Relations / Foreign Keys

- `entreprises.user_id` → `users.id`
- `analyses_prelevement.entreprise_id` → `entreprises.id`
- **[ À compléter : Toutes les FK ]**

##### Index

**[ À compléter : Index pour optimisation ]**

---

### 4.6 Diagramme de composants - Architecture

**🎯 Ticket** : SFB-46  
**👤 Responsable** : À assigner  
**📦 Livrable** : Diagramme de composants

---

**[ 📝 À COMPLÉTER PAR [NOM] ]**

Créer le **diagramme de composants** représentant l'architecture technique :

##### Description

[À compléter : Architecture 3-tiers de SaveFunds]

##### Diagramme

![Diagramme de composants](images/component-diagram.png)

##### Composants principaux

**Frontend (React)**
- Components
- Services
- Store (Redux/Context)

**Backend (Spring Boot)**
- Controllers
- Services
- Repositories
- Security

**Base de données (PostgreSQL)**

**Services externes**
- EmailService (SMTP)
- [Autres si nécessaire]

##### Interfaces / APIs

**[ À compléter : Interfaces entre composants ]**

---

### 4.7 Diagramme de packages

**🎯 Ticket** : SFB-49  
**👤 Responsable** : Wilfred Tiwa  
**📦 Livrable** : Diagramme de packages

---

**[ 📝 À COMPLÉTER PAR WILFRED TIWA ]**

Créer le **diagramme de packages** de l'application backend :

##### Description

[À compléter : Organisation des packages Java/Spring Boot]

##### Diagramme

![Diagramme de packages](images/package-diagram.png)

##### Structure des packages

```
com.profacile.savefunds
├── config
│   ├── SecurityConfig
│   └── DatabaseConfig
├── controller
│   ├── AuthController
│   ├── EntrepriseController
│   └── AnalyseController
├── service
│   ├── AuthService
│   ├── EntrepriseService
│   └── AnalyseService
├── repository
│   ├── UserRepository
│   ├── EntrepriseRepository
│   └── AnalyseRepository
├── model
│   ├── User
│   ├── Entreprise
│   └── AnalysePrelevement
├── dto
│   ├── LoginRequest
│   ├── AnalyseRequest
│   └── AnalyseResponse
└── exception
    ├── ResourceNotFoundException
    └── UnauthorizedException
```

**[ À compléter : Détailler chaque package ]**

##### Dépendances entre packages

**[ À compléter : Flèches de dépendances ]**

---

## 5. Conclusion

Cette analyse fonctionnelle constitue la base solide pour le développement de SaveFunds durant les sprints 2 à 12.

Tous les diagrammes et spécifications documentés dans ce document serviront de référence pour :
- L'implémentation backend (Sprints 2-8)
- Le développement frontend (Sprints 9-12)
- Les tests fonctionnels (Sprint 8 et 12)

### 5.1 Prochaines étapes

**Sprint 2-3** : Setup infrastructure
- Configuration GitLab CI/CD
- Mise en place Docker
- Création base de données PostgreSQL

**Sprint 4-6** : Développement backend
- API REST
- Services métier
- Sécurité (JWT, BCrypt)

**Sprint 7-8** : Tests backend
- Tests unitaires
- Tests d'intégration
- Tests sécurité

**Sprint 9-12** : Développement frontend
- Interface React
- Intégration API
- Tests E2E
- Déploiement production

### 5.2 Validation

**Document complété par** : 14 stagiaires Profacile SRL  
**Supervisé par** : Christian Sandjong Motio

**Date validation** : _______________

**Signature Chef de projet** : _______________

---

**Profacile SRL - Mars 2026**

---

## 📝 Notes de complétion

**Instructions pour les stagiaires** :

1. Trouver VOTRE section (chercher votre nom)
2. Remplacer `[ 📝 À COMPLÉTER PAR [VOTRE NOM] ]` par votre contenu
3. Ajouter vos diagrammes dans `docs/images/`
4. Compléter TOUTES les parties marquées `[À compléter]`
5. Supprimer les commentaires `⚠️` et `✅` après correction
6. Commit avec message : `feat(SFB-XX): completion section X.X`
7. Push et créer Merge Request

**Format images** :
- PNG uniquement
- Minimum 1200px de large
- Nom explicite : `sequence-login.png`, `class-diagram.png`
- Placer dans `docs/images/`

**Référence syntaxe** : [Markdown Guide](https://www.markdownguide.org/)

---

**Fin du document**
