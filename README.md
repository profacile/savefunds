
--# SaveFunds

SaveFunds est une application de vigilance financiere pour PME/SRL belges.

Le projet est organise en monorepo :

- `savefunds-backend` : API Spring Boot, moteur de vigilance, ingestion de donnees financieres.
- `savefunds-angular` : nouveau frontend Angular oriente demonstration metier.
- `savefunds-frontend` : ancien frontend Vue, conserve comme reference visuelle.

## Nouvelle orientation metier

SaveFunds ne se limite plus a demander quatre chiffres a l'utilisateur. Le backend est concu pour ingerer des donnees financieres structurees, les normaliser dans un `FinancialSnapshot`, puis les interpreter via un moteur de vigilance.

Sources prises en charge dans le MVP backend :

- encodage manuel ;
- extrait bancaire CSV normalise ;
- export comptable CSV normalise base sur des comptes belges ;
- providers mockes BNB, PSD2/Open Banking et parsing de bilan.

## Commandes backend

```powershell
cd savefunds-backend
docker compose up -d postgres
$env:SPRING_PROFILES_ACTIVE="dev"
.\mvnw.cmd spring-boot:run
```

Tests :

```powershell
cd savefunds-backend
.\mvnw.cmd test
```

## Commandes frontend Angular

```powershell
cd savefunds-angular
pnpm install
pnpm start
```

Build :

```powershell
cd savefunds-angular
pnpm exec ng build
```

L'application Angular attend le backend sur `http://localhost:8080`.
