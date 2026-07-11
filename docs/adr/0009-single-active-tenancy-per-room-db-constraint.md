# 0009 — One active tenancy per room, enforced by a database constraint

**Status:** Accepted
**Date:** 2026-07-12

## Context

For the MVP a room holds one tenant at a time (shared/multi-occupancy rooms are
deferred — see the operations spec). That rule — *at most one active tenancy per
room* — must be enforced somewhere. Two clients starting a tenancy for the same
empty room at nearly the same moment is the concurrency hazard: an application
check that reads "is this room free?" and then writes can let both writes through
in the gap between the read and the write (check-then-act race). This is the same
class of problem the superseded booking design solved for double-occupancy.

## Considered Options

- **Application-level check-then-act** — the service reads whether an active
  tenancy exists, and if not, creates one. Pro: simple, no database feature
  needed; con: not safe under concurrency — two simultaneous requests can both
  read "free" before either writes, producing two active tenancies for one room.
- **Redis / distributed lock** — take a lock on the room id before writing. Pro:
  serializes the operation; con: adds infrastructure and failure modes (lock
  expiry, releases) orthogonal to the learning goal, and still leaves the
  database able to hold invalid data if the lock logic is ever bypassed.
- **Database constraint** — a partial uniqueness rule: at most one tenancy row
  per room may be in the active state. Pro: the database is the single arbiter,
  correct even under concurrency and even if application code has a bug; the
  second writer simply fails and the service turns that failure into a clean
  409 conflict; con: requires knowing the database's partial-unique-index
  feature, and the conflict surfaces as an exception to be handled rather than a
  pre-check.

## Decision

We will enforce *at most one active tenancy per room* with a **database-level
partial uniqueness constraint** — a uniqueness rule that applies only to rows in
the active state. The service attempts the insert and translates a constraint
violation into a `409 Conflict` ("room is already occupied"), rather than relying
on a check-then-act read.

## Consequences

Easier: correctness is guaranteed by the database regardless of concurrency or
application bugs; there is exactly one place the rule lives; no extra
infrastructure.

Harder: the constraint's exact shape depends on how "active" is expressed on the
tenancy (e.g. a null end date), so the tenancy model and this constraint are
designed together; and the service must catch the constraint violation and map
it to a clear conflict response instead of a generic error. Multi-occupancy
rooms would require revisiting this (a count-based rule), and are deferred.
