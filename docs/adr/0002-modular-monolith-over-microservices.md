# 0002 — Modular monolith over microservices

**Status:** Accepted
**Date:** 2026-07-05

## Context

The backend needs an overall architectural style before any package structure
gets created in Task 0. The design spec is explicit that "modular monolith
architecture" is itself one of the things this project exists to practice
(alongside JWT auth and Redis usage), and the domain has four clearly
separable concerns — auth, properties, rooms, bookings — that also introduce
a real cross-module dependency (rooms depends on bookings for derived
availability). This is a solo learning project with no production traffic,
no team to split across services, and no operational infrastructure (service
discovery, container orchestration, etc.) already in place.

## Considered Options

- **Microservices** (separate deployable service per module) — pro: true
  runtime isolation, independent scaling and deployment per service; con:
  the operational overhead (inter-service network calls, distributed data
  consistency, service discovery) is entirely orthogonal to the learning
  goals here — auth, ownership chains, and DB-enforced concurrency safety —
  and would spend the project's limited time on infrastructure plumbing
  instead of those concepts. Distributed transactions would also complicate
  the double-booking constraint, which the spec deliberately wants solved by
  a single database constraint.
- **Modular monolith** (single deployable, enforced module boundaries in
  code) — pro: one process to run and debug locally, no network calls
  between modules, and the discipline of keeping modules from reaching into
  each other's internals is still practiced through package boundaries and
  explicit dependency rules; con: none of the independent-scaling or
  independent-deployment benefits of microservices apply — irrelevant at
  this project's scale.

## Decision

We will build the backend as a single Spring Boot modular monolith with
explicit module boundaries — `auth`, `properties`, `rooms`, `bookings`, and a
shared module for cross-cutting concerns — rather than as microservices.

## Consequences

Easier: one process to run, debug, and deploy locally; no network calls or
distributed transactions between modules; a simpler local dev loop that
matches the README's "Running locally" goal.

Harder: module boundaries are enforced only by convention and package
structure, not by a network boundary, so discipline is required to keep
(for example) `bookings` from reaching directly into `rooms`'s persistence
internals instead of going through its public surface. `docs/architecture.md`'s
"Module dependency rules" section is where those allowed edges get written
down explicitly, and that document is what keeps this discipline honest as
the project grows.
