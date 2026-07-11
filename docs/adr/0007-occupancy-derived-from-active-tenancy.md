# 0007 — Room occupancy derived from an active tenancy, never stored

**Status:** Accepted
**Date:** 2026-07-12

## Context

A room is either occupied or available, and the app needs to know which — to show
occupancy, and to prevent assigning a tenant to an already-occupied room. The
question is whether "occupied" is a stored flag on the room or a value derived
from the tenancy records. This is the same design point the superseded booking
design faced for availability; the reasoning carries over unchanged.

## Considered Options

- **Store an `occupied` flag on the room** — pro: reading occupancy is a single
  field; con: it's a second source of truth that must be kept in sync with the
  tenancy records on every move-in and move-out. Any missed update leaves the
  flag lying — a room shown occupied with no tenant, or free while someone lives
  there. Two sources of truth for one fact drift.
- **Derive occupancy from tenancies** — a room is occupied if an *active*
  tenancy exists for it (started, not yet ended). Pro: one source of truth (the
  tenancy records); occupancy cannot disagree with reality because it *is*
  reality, read live; con: occupancy is a small query rather than a field read.

## Decision

We will **derive** room occupancy by checking whether an active tenancy exists
for the room — a tenancy whose start date has arrived and whose end date is
empty or in the future. No `occupied` flag is stored on the `Room` entity. The
`tenants` module exposes a narrow facade (e.g. "is there an active tenancy for
this room") that `rooms` calls; `rooms` never reaches into tenancy storage
directly.

## Consequences

Easier: occupancy is always correct by construction; there is no sync step to
forget on move-in/move-out; the single source of truth is the tenancy record.

Harder: occupancy is computed, so it appears in a response DTO (stitched in at
read time), not as a column; and the `rooms → tenants` facade call is the one
place a room depends on the tenancy module — a dependency edge recorded in
`docs/architecture.md`.
