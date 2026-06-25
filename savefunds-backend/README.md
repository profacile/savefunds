# SaveFunds Backend

Backend Spring Boot de SaveFunds.

## Lancer PostgreSQL local

```powershell
docker compose up -d postgres
```

Le service expose PostgreSQL sur `localhost:5433`.

## Lancer l'application en local

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Avec le profil `dev`, Flyway cree le schema via `src/main/resources/db/migration`.

## Tests

```powershell
.\mvnw.cmd test
```

## Nouvelle logique metier: ingestion financiere

Le backend introduit une couche d'ingestion pour eviter que l'utilisateur saisisse toutes les donnees a la main.

Objet central:

- `FinancialSnapshot`: photo financiere normalisee d'une entreprise.

Sources MVP:

- saisie manuelle ;
- extrait bancaire CSV ;
- export comptable CSV normalise avec mapping PCMN belge simplifie.

Endpoints principaux:

```http
POST /api/v1/entreprises/{id}/financial-snapshots/manual
POST /api/v1/entreprises/{id}/financial-snapshots/import-bank-csv
POST /api/v1/entreprises/{id}/financial-snapshots/import-accounting-csv
GET  /api/v1/entreprises/{id}/financial-snapshots/latest
POST /api/v1/entreprises/{id}/financial-snapshots/simulate
```

Formats d'exemple:

- `docs/sample-bank-statement.csv`
- `docs/sample-accounting-export.csv`

Documentation detaillee:

- `../docs/BACKEND_FINANCIAL_INGESTION.md`

## Production

La configuration de production passe par variables d'environnement:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `CORS_ALLOWED_ORIGINS`

En production, Hibernate est en `validate` et Flyway versionne le schema.
