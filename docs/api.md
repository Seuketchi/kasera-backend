# API Reference

> **Operations app (post-pivot, ADR 0004), owner-only MVP (ADR 0008).** Fields
> are described in tables rather than literal JSON. Money fields are **decimals**
> (`BigDecimal`, ADR 0011) — sent/returned as normal amounts like `4500.00`, no
> conversion needed. This doubles
> as your Postman companion.

## Conventions

- **Base URL:** `http://localhost:8080` in local dev.
- **Auth:** every endpoint except `register` and `login` requires an
  `Authorization: Bearer <accessToken>` header. All authenticated endpoints act
  as the owner identified by that token.
- **Ownership:** the acting owner is always taken from the token, never from a
  request field. You cannot create or read another owner's data.
- **Resource paths** are plural nouns; nested paths express containment
  (`/properties/{id}/rooms`).
- **Money** is sent and returned as decimal values (e.g. `4500.00`).

### Error shape and the status scheme

Every error returns the same small shape: an HTTP status plus a short message.
The status carries the meaning:

| Status | Meaning |
|---|---|
| `400` | Bad input — request validation failed |
| `401` | Not authenticated — missing / invalid / expired token |
| `403` | Not yours — you're authenticated, but the resource belongs to another owner |
| `404` | Doesn't exist — no such id at all |
| `409` | Conflict — a database rule rejected the change (e.g. room already occupied, month already charged) |

The **403 vs 404** distinction is deliberate: asking for a resource that exists
but isn't yours returns 403; asking for one that doesn't exist returns 404.

## Auth  (module: `auth`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /auth/register` | none | email, password | `201` owner created | `409` if email already registered |
| `POST /auth/login` | none | email, password | `200` + access token + refresh token | `401` on bad credentials |
| `POST /auth/refresh` | none | refresh token | `200` + new access + new refresh (old refresh rotated out) | `401` if the refresh token is unknown/expired/already rotated |
| `POST /auth/logout` | bearer | — | `204` | — |
| `GET /auth/me` | bearer | — | `200` + the owner's own id, email, role | `401` |

## Properties  (module: `properties`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /properties` | owner | name, location | `201` + property | `400` on invalid input |
| `GET /properties` | owner | — | `200` + the owner's active properties | — |
| `GET /properties/{id}` | owner | — | `200` + property | `403` not yours · `404` no such id |
| `PUT /properties/{id}` | owner | name, location | `200` + updated | `403` · `404` |
| `POST /properties/{id}/deactivate` | owner | — | `204` (hides it and its rooms) | `403` · `404` |

## Rooms  (module: `rooms`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /properties/{propertyId}/rooms` | owner | label, monthlyRent, description? | `201` + room | `403` · `404` (property) |
| `GET /properties/{propertyId}/rooms` | owner | — | `200` + rooms, each with **derived occupancy** and current tenant name if occupied | `403` · `404` |
| `GET /rooms/{id}` | owner | — | `200` + room + occupancy | `403` · `404` |
| `PUT /rooms/{id}` | owner | label, monthlyRent, description? | `200` + updated (does **not** change any ongoing tenancy's locked rent) | `403` · `404` |
| `POST /rooms/{id}/deactivate` | owner | — | `204` | `403` · `404` · `409` if the room has an active tenancy |

## Tenants  (module: `tenants`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /tenants` | owner | name, phone?, email? | `201` + tenant | `400` |
| `GET /tenants` | owner | — | `200` + the owner's tenants | — |
| `GET /tenants/{id}` | owner | — | `200` + tenant | `403` · `404` |
| `PUT /tenants/{id}` | owner | name, phone?, email? | `200` + updated | `403` · `404` |
| `GET /tenants/{id}/balance` | owner | — | `200` + total outstanding across the tenant's unpaid charges | `403` · `404` |

## Tenancies  (module: `tenants`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /rooms/{roomId}/tenancies` | owner | tenantId, startDate, deposit | `201` + tenancy (rent locked from the room's current rent) | `403` · `404` · `409` if the room already has an active tenancy (ADR 0009) |
| `GET /rooms/{roomId}/tenancies` | owner | — | `200` + tenancy history for the room | `403` · `404` |
| `GET /tenancies/{id}` | owner | — | `200` + tenancy | `403` · `404` |
| `POST /tenancies/{id}/end` | owner | endDate | `204` (frees the room immediately) | `403` · `404` |

## Billing — Charges  (module: `billing`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /tenancies/{id}/charges` | owner | periodYearMonth, dueDate | `201` + charge (amount copied from the tenancy's locked rent) | `403` · `404` · `409` if that month is already charged |
| `GET /tenancies/{id}/charges` | owner | — | `200` + charges with each one's outstanding balance | `403` · `404` |
| `GET /charges/{id}` | owner | — | `200` + charge + outstanding balance | `403` · `404` |

## Billing — Payments  (module: `billing`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `POST /charges/{id}/payments` | owner | amount, paidOn, note? | `201` + payment + the charge's new balance | `403` · `404` · `400` if the amount exceeds the outstanding balance |
| `GET /charges/{id}/payments` | owner | — | `200` + payments for the charge | `403` · `404` |

## Reports  (module: `billing`)

| Method + path | Auth | Request fields | Success | Failure |
|---|---|---|---|---|
| `GET /properties/{id}/report` | owner | — | `200` + occupancy count, this month's expected vs. collected, and the list of tenants in arrears | `403` · `404` |

*(Reports are the last MVP task; the exact fields firm up when it's built.)*
