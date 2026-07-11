# 0004 — Pivot from booking marketplace to operations/management app

**Status:** Accepted
**Date:** 2026-07-12

## Context

The project began (design spec `2026-07-05-boarding-house-booking-app-design.md`,
ADRs 0001–0003) as a boarding-house *booking marketplace*: owners list
properties and rooms, boarders browse and request bookings, owners approve or
reject them. At the point of this decision only Task 0 (scaffolding) was
complete — the app boots but no domain feature exists yet, so essentially no
implementation code is tied to the booking premise.

Revisiting the direction, the builder found the more useful and interesting
product is not the *front door* (getting a boarder approved to move in) but the
*ongoing operations* of running a boarding house: tracking who lives in which
room, charging rent each month, recording payments, and seeing who owes what. A
booking marketplace models a relationship that ends the moment a booking is
approved; a management app models the tenancy that begins there.

Because no feature code exists yet, the cost of changing domain now is
re-specification and planning, not thrown-away code — making this the cheapest
possible moment to switch.

## Considered Options

- **Stay a booking marketplace** — pro: the spec and 14-task plan are already
  written; con: models only discovery and approval, not the day-to-day
  management the builder actually wants, and none of the engineering the
  builder is now after (recurring billing, money/date handling, reporting) is
  present.
- **Hybrid / superset** (keep the booking flow *and* add operations on top) —
  pro: the most realistic real-world product; con: the largest scope, doubling
  the surface for a solo learning project whose stated goal is depth over
  completeness.
- **Fresh project** (leave this repo, start a new one) — pro: clean slate; con:
  discards the reusable auth/properties/rooms design and the tooling and docs
  already set up, for no real gain when almost nothing is booking-specific yet.
- **Full pivot in place** — pro: keeps the reusable foundation (auth,
  properties, rooms, the modular-monolith structure, local dev tooling) and the
  ADRs that still hold; con: the booking spec, plan, and booking-specific design
  docs must be superseded and rewritten.

## Decision

We will re-scope this project in place from a booking marketplace to an
operations/management app for boarding-house owners: track tenants and
tenancies, charge rent on a recurring basis, record payments, and report
balances. The booking request/approval lifecycle is dropped. The `auth`,
`properties`, and `rooms` modules and the modular-monolith structure are
retained; two new modules — `tenants` and `billing` — become the core.

ADRs 0001 (two repositories), 0002 (modular monolith), and 0003 (no automated
testing) are unaffected by this decision and remain in force.

## Consequences

Easier: the reusable foundation — auth, property/room modeling, the security and
error-handling layers, and the local dev tooling — survives untouched; because
the pivot lands before any booking-specific code was written, nothing is thrown
away.

Harder: the booking spec (`specs/2026-07-05-boarding-house-booking-app-design.md`)
and phase-1 plan (`plans/2026-07-05-boarding-house-booking-app-phase1.md`) in the
second brain are now superseded and need rewriting; `architecture.md`,
`data-model.md`, and `api.md` in this repo describe the booking domain and must
be re-derived once the new scope's design decisions are settled. The new domain
also leans harder into money and date handling — recurring rent, proration,
payment ledgers, "who owes what" aggregation — which is more error-prone than
booking logic and will need its own careful decisions (and ADRs).

Follows-on, and explicitly **not** settled by this ADR: the concrete
module/entity design, how "rent owed" is modeled (generate a charge row per
period vs. compute on the fly), money representation (integer minor units vs.
`BigDecimal` — never floating point), what a tenancy is, and whether tenants log
in at all. These are captured as open questions in the draft operations spec
`specs/2026-07-12-boarding-house-operations-app-design.md` and are to be decided
(one ADR each) before a new task plan is written.
