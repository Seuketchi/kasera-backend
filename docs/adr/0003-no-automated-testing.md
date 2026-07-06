# 0003 — No automated testing

**Status:** Accepted
**Date:** 2026-07-05

## Context

Automated tests (unit and integration) are standard practice on production
codebases, so their absence here needs recording rather than assuming —
per the plan's own note, unusual decisions need ADRs most. This project's
stated purpose is rebuilding hands-on backend fluency and preparing for
technical interviews after a period of heavy AI-tool reliance; the design
spec explicitly scopes automated testing out of Phase 1, matching the same
exclusion made in the prior notes-app learning project. Each task in the
plan instead defines a manual check (via Postman/curl, or running the
Flutter app) as its definition of done.

## Decision

We will not write automated tests (unit or integration) for this project.
Each task's manual verification step, as defined in the implementation plan,
is the definition of done in its place.

## Consequences

Easier: more of the project's limited time goes toward the architecture and
domain concepts actually being practiced — ownership chains, the booking
state machine, the database concurrency constraint — instead of test-writing
mechanics; each task can move faster from build to verified-done.

Harder: there is no regression safety net. A later task (or a future Phase 2
or 3) can silently break Phase 1 behavior with nothing to catch it, and
re-verifying a task after a later change means manually re-running that
task's original manual check by hand. This is an explicit, deliberate
trade-off scoped to this solo learning project — not a recommendation for
how to build anything meant for production.
