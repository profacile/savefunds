# SaveFunds Frontend

Frontend Vue 3 / Vite de SaveFunds.

## Configuration

Creer un fichier `.env` a partir de `.env.example`:

```powershell
Copy-Item .env.example .env
```

Variable principale:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## Developpement

```powershell
npm install
npm run dev
```

## Verification

```powershell
npm run build
```

La CI execute le type-check puis le build.
