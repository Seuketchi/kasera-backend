# The Path — Kasera (Operations App)

How to walk through this project. This guide is self-contained: the task sequence
lives here. Each task says what must exist by the end of it, how to check it
manually, and which docs/ADRs it touches.

**The loop, every task:**

```
read → decide → build → verify manually → document → commit
```

- *Read* — just-in-time, from the map in `learning-sources.md`.
- *Decide* — the big cross-cutting decisions are already ADRs (0005–0011); a task
  may still force a small local choice — name it, then write it down.
- *Build* — you write every line. The docs are your reference; read them, then
  code against them.
- *Verify* — the **Manual check** in each task is the definition of done.
- *Document* — update `data-model.md` / `api.md` to match what you built.
- *Commit* — after the manual check passes, docs included.

Key references while building: `architecture.md`, `conventions/kotlin-spring.md`,
`data-model.md`, `api.md`, `adr/`.

---

## Ordering note — domain first, auth last

This path builds the **domain (rooms → tenants → billing) first**, then adds
**auth last**. That's deliberate: you get the interesting app working fast without
JWT ceremony up front. The tradeoff, stated honestly:

- Until Task 8, endpoints are **unauthenticated** and there is **no owner** — you
  test against a single implicit account.
- `Property` and `Tenant` get their `ownerId` field, and every ownership check
  (the 403-vs-404 rule, the room→property→owner walk), **when auth lands in Task
  8**. The `data-model.md` / `api.md` docs describe the *finished* owner-scoped
  design; these early tasks build toward it, and Task 8 wires the owner in.

---

## Task 0 — Scaffolding ✅

Spring Boot Kotlin project boots; Postgres and Redis run via `compose.yaml`. Done.

## Task 1 — Properties  *(module: properties)*

The `Property` entity (name, location, active flag) plus create / list / get /
edit / deactivate. Deactivate instead of delete (ADR 0010). No `ownerId` yet —
that arrives with auth (Task 8).

**Manual check:** create a property; edit it; deactivate it and confirm it drops
out of the active list; a made-up id returns `404`.
**Docs:** `data-model.md` (Property), `api.md` (properties).

## Task 2 — Rooms  *(module: rooms)*

The `Room` entity — rent as `BigDecimal` (ADR 0011), under a property — plus
create / list-under-property / get / edit / deactivate. Deactivating a property
hides its rooms. Occupancy is **not** stored (ADR 0007); it stays blank until
tenancies exist (Task 4).

**Manual check:** add rooms under a property; deactivating the property hides its
rooms from the active list; editing a room's rent works.
**Docs:** `data-model.md` (Room — note the no-stored-occupancy absence),
`api.md` (rooms).

## Task 3 — Tenants  *(module: tenants)*

The `Tenant` entity — records you manage (not users, ADR 0008): name, optional
phone/email. Create / list / get / edit.

**Manual check:** create a tenant; edit it; list shows it.
**Docs:** `data-model.md` (Tenant), `api.md` (tenants).

## Task 4 — Tenancies: assignment, the DB constraint, occupancy, move-out  *(module: tenants)*

The heart of the domain. The `Tenancy` entity links a tenant to a room and locks
in the rent at move-in. Enforce **one active tenancy per room** with the database
constraint (ADR 0009) — attempt the insert, translate the violation into `409`.
Build move-out (set end date), and the derived-occupancy check rooms will call
(ADR 0007), then wire occupancy into the rooms responses.

**Read first:** the PostgreSQL *partial indexes* chapter.
**Manual check:** assign a tenant to a room; a second assignment to the same room
returns `409`; the room now reads occupied; end the tenancy and the room reads
available again immediately; a room with an active tenancy can't be deactivated.
**Docs:** `data-model.md` (Tenancy + lifecycle + the constraint), `api.md`
(tenancies).

## Task 5 — Billing: charges  *(module: billing)*

The `Charge` entity. Generate a rent charge for a tenancy for a given month
(amount copied from the tenancy's locked rent, ADR 0006). One charge per tenancy
per month (unique constraint).

**Manual check:** generate a charge; the same month again returns `409`; the
amount matches the tenancy's locked rent even after the room's rent was edited.
**Docs:** `data-model.md` (Charge), `api.md` (charges).

## Task 6 — Billing: payments & balances  *(module: billing)*

The `Payment` entity, recorded against a charge. A charge's outstanding balance is
its amount minus its payments; a tenant's balance sums their unpaid charges.
Reject a payment larger than the outstanding balance (MVP — no overpayment).

**Manual check:** a partial payment lowers the charge's balance; an over-payment
is rejected; the tenant balance reflects all unpaid charges.
**Docs:** `data-model.md` (Payment), `api.md` (payments, tenant balance).

## Task 7 — Reports  *(module: billing)*

A per-property report: current occupancy, this month's expected vs. collected
rent, and who's in arrears. (Optional stretch: cache it in Redis, cache-aside.)

**Manual check:** the numbers reconcile by hand against the tenancies, charges,
and payments.
**Docs:** `api.md` (report).

## Task 8 — Auth + the ownership retrofit  *(module: auth, then across all)*

Now secure it. Build the `User` entity (owner, BCrypt-hashed password, unique
email), registration, login issuing a JWT access token, the JWT filter, and
`GET /auth/me`. **Then retrofit ownership:** add `ownerId` to `Property` and
`Tenant`, source it from the authenticated identity, and add the ownership checks
across Tasks 1–7 — the room→property→owner walk and the 403-vs-404 distinction.

**Read first:** OWASP Password Storage + JWT cheat sheets; Spilcă on the filter
chain.
**Manual check:** register + log in; a protected endpoint works with the token and
returns `401` without it; owner B gets `403` touching owner A's property/room/
tenant/tenancy, and `404` for a non-existent id.
**Docs:** `data-model.md` (User + the ownerId fields), `api.md` (auth + confirm
the ownership rules across every endpoint).

## Task 9 — Refresh tokens, Redis, logout  *(module: auth)*

Add refresh tokens stored in Redis (token-as-key), rotation on refresh, single
active session per user, and logout.

**Manual check:** refresh succeeds; reusing the old refresh token after a refresh
fails; a second login invalidates the first session; logout kills the token.
**Docs:** `conventions/redis.md` (confirm key scheme), `api.md` (refresh, logout).

## Tasks 10–12 — Flutter owner app

Owner login, then property/room management, then tenant/tenancy management and the
billing/collection screens. Owner-only.
**Docs:** `conventions/flutter.md` (structure + the screens↔endpoints table).

## Task 13 — End-to-end pass

Walk the whole flow fresh: register + log in, add a property + room, add a tenant,
start a tenancy, generate a charge, record a payment, read the report, end the
tenancy, confirm the room frees up. Note anything awkward as Phase 2 candidates.
Final doc pass — fix any drift while it's fresh.

---

## Standing rules

- **Commit after every task** — code and docs together, once the manual check
  passes.
- **One decision per ADR;** accepted ADRs are immutable — supersede, don't edit.
- **If a doc section stays empty two tasks past its slot,** write it or delete it.
