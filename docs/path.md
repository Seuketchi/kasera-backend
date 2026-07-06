# The Path — Phase 1 Guide

How to walk through this project. The task list itself lives in the plan
(`~/personal/second-brain/docs/superpowers/plans/2026-07-05-boarding-house-booking-app-phase1.md`);
this guide adds the loop around each task: what to read first, what decisions
you'll face, which docs those decisions land in, and when to commit.

**The loop, every task:**

```
read → decide (ask tutor about options if stuck) → build → verify manually → document → commit
```

- *Read* — just-in-time, from the map in `learning-sources.md`. Never more than the current task needs.
- *Decide* — before coding, name the choices the task forces. If you don't know the standard options, ask (Claude session): "what are the standard options for X and their tradeoffs?" — get options, not answers.
- *Build* — you write every line. No generated code, per the spec.
- *Verify* — each task's **Manual check** section in the plan is the definition of done.
- *Document* — ADRs for decisions, plus the doc updates listed per task below. Write them same-day; the context evaporates fast.
- *Commit* — after the manual check passes, docs included.

---

## Before Task 0 — foundations on paper

- **Write ADR 0001:** one repo or two (backend + Flutter)? This also decides where `git init` runs.
- **Write ADR 0002:** modular monolith over microservices (backfill from the spec's reasoning).
- **Write ADR 0003:** no automated testing for this project (unusual decisions need ADRs most).
- Add all three to the ADR index table.

## Task 0 — scaffolding

- **Read:** Spring guide *"Building web applications with Spring Boot and Kotlin"*; Grzybek's *Modular Monolith: A Primer*.
- **Decide:** package name; module layout (this is where the modular-monolith idea becomes real folders).
- **Document:** start `README.md` (stack + running locally); start `docs/architecture.md` (modules + dependency rules).

## Task 1 — user identity & registration

- **Read:** OWASP *Password Storage* cheat sheet; Spring Academy REST API course if you want the guided version.
- **Decide:** package layout inside a module; entity-vs-DTO boundary; where validation lives; duplicate-email error shape.
- **Document:** start `conventions/kotlin-spring.md` (layout, DI, entity/DTO sections); start `docs/data-model.md` (User); start `docs/api.md` (conventions section + register endpoint).

## Task 2 — login & access tokens

- **Read:** OWASP *JWT* cheat sheet; jwt.io intro; Spilcă (*Spring Security in Action*) on the filter chain.
- **Decide:** token lifetime; what claims go in; how 401s are produced consistently.
- **Document:** `api.md` (login, who-am-I); `conventions/kotlin-spring.md` (error handling section — the 401/403/404 rule starts here).

## Task 3 — refresh tokens, Redis, logout

- **Read:** OWASP *Session Management* cheat sheet; redis.io data-types tutorial + key naming conventions.
- **Decide:** the key naming scheme (token-as-key, session index); TTLs.
- **Write ADR:** refresh rotation with token-as-key in Redis; single active session per user (one ADR or two — your call, but "one decision per ADR" suggests two).
- **Document:** start `conventions/redis.md` (key scheme + TTL policy); `api.md` (refresh, logout).

## Task 4 — properties

- **Decide:** how ownership-from-authenticated-identity is enforced; how role checks are expressed.
- **Write ADR:** delist instead of hard-delete.
- **Document:** `data-model.md` (Property); `api.md` (properties endpoints); `architecture.md` if the module boundary surprised you.

## Task 5 — rooms

- **Decide:** where the ownership-chain walk lives (this pattern repeats in Tasks 7–8 — decide once).
- **Document:** `data-model.md` (Room — including the deliberate absence of a stored availability flag); `api.md` (rooms endpoints).

## Task 6 — booking requests (pending only)

- **Write ADR:** price locked into the booking at request time.
- **Document:** `data-model.md` (Booking + the lifecycle state table); `api.md` (request + my-bookings endpoints).

## Task 7 — approval, the DB constraint, auto-reject

- **Read (before, not during):** PostgreSQL docs — *partial indexes* chapter; skim *transaction isolation* for the why.
- **Write ADR:** double-booking prevented by a database constraint — the full MADR treatment, three considered options (app-level check-then-act, Redis lock, DB partial unique index).
- **Write ADR:** auto-reject competing pending requests on approval.
- **Document:** `data-model.md` (integrity rules section); `api.md` (owner request views, approve, reject — including the constraint-violation error).

## Task 8 — cancel, end, derived availability

- **Write ADR:** availability is derived, never stored.
- **Document:** `data-model.md` (what actually frees a room — both exits); `api.md` (cancel, end); `architecture.md` (the rooms→bookings dependency edge is now real — draw it).

## Task 9 — search

- **Read:** skim the Zalando guidelines on query parameters / filtering.
- **Document:** `api.md` (search parameters + the pending-rooms-still-appear rule).

## Task 10 — Redis search caching

- **Read:** redis.io caching patterns (cache-aside).
- **Decide:** cache key derivation from filter combinations (normalization/ordering); invalidation vs TTL-only — the plan explicitly says "decide which and note why."
- **Write ADR:** search cache keying + invalidation strategy.
- **Document:** `conventions/redis.md` (search cache key type + TTL row).

## Task 11 — Flutter: auth screens

- **Read:** nothing new — this is your day job. The exercise is choosing what NOT to carry over.
- **Decide:** project structure and state management, simpler than work's stack — and why.
- **Document:** start `conventions/flutter.md`; consider an ADR if the deviation reasoning feels decision-shaped.

## Tasks 12–13 — owner & boarder screens

- **Document:** the screens↔endpoints table in `conventions/flutter.md` as you go.

## Task 14 — end-to-end pass

- Walk the full flow fresh, per the plan.
- **Document:** anything awkward goes in a `## Phase 2 candidates` note (README or a scratch doc) — that list seeds the next spec.
- Final pass over every doc: does each one match what was actually built? Fix drift now, while you still remember.

---

## Standing rules

- **Commit after every task** — code and docs together, once the manual check passes.
- **One decision per ADR;** accepted ADRs are immutable — supersede, don't edit.
- **If a doc section stays empty two tasks past its slot,** either write it or delete the section — a skeleton that lingers is noise.
- **When stuck on a choice:** ask for options and tradeoffs, decide yourself, write it down. Never ask for the answer.
