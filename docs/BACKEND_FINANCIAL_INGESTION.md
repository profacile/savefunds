# SaveFunds - nouvelle couche metier backend

## Objectif

Le backend ne repose plus uniquement sur des donnees encodees manuellement.
Il introduit une couche d'ingestion qui lit des donnees financieres structurees,
les normalise dans un `FinancialSnapshot`, puis les transmet au moteur de
vigilance.

```text
CSV bancaire / CSV comptable / saisie manuelle
        |
        v
FinancialDataExtractor
        |
        v
FinancialSnapshot
        |
        v
VigilanceEngine
        |
        v
Decision tricolore + recommandations
```

## Formats MVP

### 1. CSV bancaire normalise

Colonnes attendues :

```csv
date,description,amount,balance
2026-06-01,Client facture 001,5000,12000
2026-06-05,Loyer,-1200,10800
2026-06-10,TVA,-2500,8300
```

Donnees extraites :

- total des entrees ;
- total des sorties ;
- solde final ;
- estimation des charges mensuelles ;
- date du snapshot.

Endpoint :

```http
POST /api/v1/entreprises/{entrepriseId}/financial-snapshots/import-bank-csv
Content-Type: multipart/form-data
```

### 2. CSV comptable normalise

Colonnes attendues :

```csv
accountCode,label,amount
70,Ventes,120000
61,Services et biens divers,-30000
62,Remunerations,-40000
550,Banque,15000
416,Compte courant dirigeant,-3000
440,Fournisseurs,-8000
400,Clients,12000
```

Mapping belge simplifie :

| Comptes | Signification SaveFunds |
| --- | --- |
| `70` | chiffre d'affaires |
| `60/61/62/63/64` | charges |
| `550/551/552/53/57` | tresorerie |
| `400` | creances clients |
| `440` | dettes fournisseurs |
| `416/489` | compte courant dirigeant / associes |

Endpoint :

```http
POST /api/v1/entreprises/{entrepriseId}/financial-snapshots/import-accounting-csv
Content-Type: multipart/form-data
```

## Simulation de decision

Une fois un `FinancialSnapshot` cree, le backend peut simuler une decision :

```http
POST /api/v1/entreprises/{entrepriseId}/financial-snapshots/simulate
```

Exemple :

```json
{
  "type": "RETRAIT_DIRIGEANT",
  "amount": 3000,
  "plannedDate": "2026-07-01"
}
```

Le moteur retourne :

- tresorerie avant/apres ;
- montant maximum recommande ;
- couverture en mois apres decision ;
- indicateurs individuels ;
- decision globale ;
- recommandations.

## Limites assumees

Le MVP ne pretend pas lire tous les formats comptables du marche. Il definit
deux formats normalises et montre l'architecture d'extension via
`FinancialDataExtractor`.

Extensions prevues :

- parser XLSX ;
- parser PDF standardise ;
- API BCE/BNB ;
- API bancaire PSD2/Open Banking avec consentement explicite.
