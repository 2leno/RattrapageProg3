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

## Tests curl

```bash
# 1. Liste de toutes les courses
curl http://localhost:8080/trips

# 2. Filtrer les courses par chauffeur
curl "http://localhost:8080/trips?driverId=d-001"

# 3. Filtrer les courses par periode
curl "http://localhost:8080/trips?from=2026-04-01&to=2026-04-30"

# 4. Filtrer par chauffeur ET periode
curl "http://localhost:8080/trips?driverId=d-001&from=2026-04-01&to=2026-04-30"

# 5. Creer une course (PUT upsert)
curl -X PUT http://localhost:8080/trips/t-099 \
  -H "Content-Type: application/json" \
  -d '{"driverId":"d-001","vehicleId":"v-001","tripDate":"2026-06-01","departureCity":"Antananarivo","arrivalCity":"Toamasina","distanceKm":350,"billedAmount":2800000}'

# 6. Modifier le statut d'une course
curl -X PUT http://localhost:8080/trips/t-099/status \
  -H "Content-Type: application/json" \
  -d '{"status":"CANCELLED"}'

# 7. Chiffre d'affaires et commission d'un chauffeur
curl "http://localhost:8080/drivers/d-001/revenue?from=2026-04-01&to=2026-04-30"

# 8. Statistiques de rentabilite d'un chauffeur
curl http://localhost:8080/drivers/d-001/statistics

# 9. Chauffeur le plus rentable sur une periode
curl "http://localhost:8080/statistics/top-earning-driver?from=2026-04-01&to=2026-04-30"
```

## Spec API

La specification OpenAPI se trouve dans `docs/api.yml`.
