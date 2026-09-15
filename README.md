# KOFIA — Cooperative de transporteurs

API REST de la cooperative KOFIA pour le suivi des courses de fret et le calcul des reversements mensuels.

## Auteur

- **Lucas Andrianina** — Etudiant STD24028

## Endpoints

| Methode | URL | Description |
|---|---|---|
| GET | `/trips` | Liste des courses (filtres optionnels : driverId, from, to) |
| PUT | `/trips/{tripId}` | Cree ou remplace une course |
| PUT | `/trips/{tripId}/status` | Modifie le statut d'une course |
| GET | `/drivers/{driverId}/revenue` | Chiffre d'affaires et commission (from, to) |
| GET | `/drivers/{driverId}/statistics` | Statistiques de rentabilite |
| GET | `/statistics/top-earning-driver` | Chauffeur le plus rentable (from, to) |

## Execution

```bash
# Creer la base
psql -U postgres -d postgres -c "CREATE DATABASE kofia;"
psql -U postgres -d kofia -f db/schema.sql
psql -U postgres -d kofia -f db/data.sql

# Lancer l'application
mvn spring-boot:run
```

L'API est accessible sur `http://localhost:8080`.

## Spec API

La specification OpenAPI se trouve dans `docs/api.yml`.
