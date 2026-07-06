# 0001 — Separate repositories for backend and client

**Status:** Accepted
**Date:** 2026-07-05

## Context

Phase 1 has two codebases: a Spring Boot + Kotlin modular-monolith backend and a
Flutter client. They use unrelated toolchains (Gradle/JVM vs. the Flutter/Dart
SDK), unrelated build artifacts and `.gitignore` needs, and will eventually
deploy to completely different targets (a server vs. app stores/devices).
Task 0 of the plan requires deciding this before `git init` runs, since it
determines whether that's one `git init` or two.

## Considered Options

- **Monorepo** (single repo, `backend/` and `client/` top-level directories) —
  pro: one place for shared docs/ADRs, atomic commits when an API change and
  its client update land together; con: mixes two unrelated toolchains in one
  working tree, CI would need path filtering, and neither side gets a commit
  history that's purely its own.
- **Two separate repos** — pro: each side's tooling, `.gitignore`, and commit
  history stays clean and independent, and it mirrors how these would
  actually be owned/deployed in a real product team; con: a change that
  touches both the API contract and the client needs two coordinated commits
  instead of one, and shared documentation needs an agreed-upon home.

## Decision

We will use two separate repositories — one for the Spring Boot/Kotlin
backend, one for the Flutter client — rather than a single monorepo.

## Consequences

Easier: independent toolchains and `.gitignore` rules, independent commit
history and release cadence per side, and a closer match to how backend and
mobile ownership would actually split on a real team.

Harder: a change spanning both the API and the client (e.g. adding a field to
a response the app consumes) now requires two commits in two repos instead of
one atomic commit, with a window where they can be out of sync. This `docs/`
folder — covering both sides — lives in the backend repo, since the backend
is the source of truth for the API contract; the Flutter repo's own
`conventions/flutter.md` will reference decisions recorded here rather than
duplicating them.
