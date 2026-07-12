# Conventions — Flutter

> **Draft (operations app).** Written before the Flutter tasks (11–13) exist in
> code, as a plausible lighter-weight structure. Most likely to change once
> actually building — real Flutter instincts from work will surface things this
> draft didn't anticipate. Owner-only client (ADR 0008).

## Project structure

By-feature folders, not by-layer: `auth/`, `properties/`, `rooms/`, `tenants/`,
`billing/` — each holding its own screens, small widgets, and a single
service/repository file for that feature's API calls. No separate
`data`/`domain`/`presentation` layering — this is a solo learning app, and that
ceremony pays off on larger teams, not here. (Deliberately simpler than the
work app's Clean Architecture — noting the deviation is the point.)

## State management

Plain `setState` for screen-local state; a single lightweight `ChangeNotifier`
(via `provider`) for state genuinely shared across screens (e.g. the logged-in
owner). The line not to cross without revisiting this doc: if a third
cross-screen concern shows up needing shared state, that's the signal to
reconsider rather than bolting on another ad-hoc notifier.

## API client

A single `Dio` instance, base URL pointed at the local backend, built once in an
`api/` folder. An interceptor attaches the access token to every outgoing request
and, on a `401`, attempts one refresh-and-retry before giving up. Tokens live in
`flutter_secure_storage` — never `SharedPreferences`, which is unencrypted local
storage unsuitable for credentials.

## Money on the client

The backend speaks **decimals** (`BigDecimal`, ADR 0011) — amounts come and go as
normal values like `4500.00`, no ÷100 conversion needed. Never do money math in
floating point on the client either; use a decimal-safe type for any arithmetic.

## Screens ↔ backend mapping

| Screen | Endpoint(s) | Notes |
|---|---|---|
| Login | `POST /auth/login`, `/auth/refresh` | Task 11 |
| Properties list / edit | `GET/POST/PUT /properties`, deactivate | Task 12 |
| Rooms under a property | `GET/POST/PUT /properties/{id}/rooms`, room deactivate | Task 12; show derived occupancy |
| Tenants list / edit | `GET/POST/PUT /tenants` | Task 13 |
| Assign / end tenancy | `POST /rooms/{id}/tenancies`, `POST /tenancies/{id}/end` | Task 13; handle the `409` room-occupied case |
| Billing / collection | `POST /tenancies/{id}/charges`, `POST /charges/{id}/payments`, `GET /tenants/{id}/balance` | Task 13 |
| Property report | `GET /properties/{id}/report` | Task 13 |
