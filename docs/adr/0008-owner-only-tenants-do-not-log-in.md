# 0008 — Owner-only MVP: tenants do not log in

**Status:** Accepted
**Date:** 2026-07-12

## Context

The app needs to decide who its users are. In the operations domain there are two
plausible human roles: the **owner** who runs the boarding house, and the
**tenant** who lives there. Whether tenants are *users with accounts* or merely
*records the owner manages* changes the auth model, the API surface, and the
entire client — so it must be settled before the `auth` and `tenants` modules are
built.

## Considered Options

- **Tenants also log in** — tenants have accounts and can see their own balance,
  charges, and payment history. Pro: the most complete product; con: it roughly
  doubles the surface — a second registration/login path, a whole tenant-facing
  set of screens and endpoints, and per-tenant authorization on every read — for
  a solo learning MVP whose goal is depth over breadth.
- **Owner-only** — the only users are owners; tenants are records an owner
  creates and manages. Pro: one role, one auth path, one client audience; every
  authorization check reduces to "does this owner own this thing"; the MVP stays
  finishable. Con: no self-service for tenants; a tenant portal is a later phase.

## Decision

For the MVP, **only owners are users.** Tenants are records managed by an owner,
not account holders — they do not register, log in, or have any authenticated
access. The `User` entity keeps a `role` field so a `TENANT` (or `STAFF`) role
can be added later without a migration of the identity model, but the MVP issues
only owner identities.

## Consequences

Easier: a single authentication path and a single client audience; every
authorization question is the one ownership check ("does the authenticated owner
own this property / room / tenant / tenancy"); the MVP scope stays small.

Harder: there is no tenant self-service — a tenant cannot check their own
balance; the owner is the only one who sees anything. Adding a tenant portal
later is a real piece of new work (a second auth path and a tenant-facing API),
explicitly deferred to a future phase and noted in the operations spec.
