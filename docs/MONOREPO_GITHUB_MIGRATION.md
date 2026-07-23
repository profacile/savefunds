# Migration GitHub vers un monorepo

Objectif : remplacer les anciens depots separes par un seul depot `savefunds`, avec le backend Spring Boot et le frontend Angular.

## Etat cible

```text
savefunds/
  savefunds-backend/
  savefunds-angular/
  docs/
  .github/workflows/ci.yml
  README.md
```

## Procedure recommandee

1. Creer un nouveau depot GitHub `profacile/savefunds`.
2. Garder les anciens depots en archive ou en lecture seule.
3. Dans le dossier racine local `savefunds`, supprimer les `.git` internes uniquement apres sauvegarde.
4. Initialiser le nouveau depot au niveau racine.
5. Committer le backend et le frontend Angular comme sous-dossiers du monorepo.

Commandes indicatives :

```powershell
cd C:\Users\SHH8701\opt\personnal\PROFACILE_SRL\savefunds

Rename-Item savefunds-backend\.git .git-backup-backend

git init
git add .
git commit -m "chore: migrate SaveFunds to monorepo"
git branch -M main
git remote add origin https://github.com/profacile/savefunds.git
git push -u origin main
```

Ne pas supprimer les anciens depots GitHub tant que le nouveau depot n'a pas ete verifie.
