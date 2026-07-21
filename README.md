# Atlas — Hotel Service

> Owns the hotel catalog (hotels + room types) and publishes catalog events.

Part of **[Atlas](https://github.com/atlas-event-lab)**.

## Responsibilities

- Manage hotels and room types (CRUD) and expose a service-readable room-type price endpoint
  (ADR-0005).
- Publish catalog events so Inventory can materialize per-night availability and Search can
  build its read model.

## Tech

Java 21 · Spring Boot · Spring Data JPA · PostgreSQL (`hotel_db`) · Kafka · Keycloak JWT.

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/hotels` · `/api/v1/hotels/{hotelId}` | List / fetch hotels |
| POST · PUT · DELETE | `/api/v1/hotels` · `/api/v1/hotels/{hotelId}` | Manage catalog |
| GET | `/api/v1/hotels/{hotelId}/room-types/{roomTypeId}/price` | Room-type price (ADR-0005) |
| POST | `/api/v1/hotels/reconciliation` | Catalog resync trigger (ADR-0026) |

## Events

**Produces:** `hotel.created`, `hotel.updated`, `hotel.deleted`. **Consumes:** none
(catalog source of truth).

## Data

Owns `hotel_db` (database-per-service). Price lives at the room-type level.

## Patterns

Transactional outbox · catalog resync — republish hotels/room-types (+ derived night
calendar) for read-model rebuild beyond retention (ADR-0026).

## Run locally

```bash
docker compose up hotel-service
```

Env: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`, `KEYCLOAK_ISSUER_URI`.

## License

Apache-2.0 — see [`LICENSE`](./LICENSE).
