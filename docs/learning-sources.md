# Learning Sources

Reference list, not a reading queue. Read just-in-time — match the source to the
task you're about to start, then immediately update the doc or ADR it informs.
Reading everything upfront is how learning projects stall at the research phase.

## Read-before-task map

| Before task | Read |
|---|---|
| Task 1 | OWASP Password Storage cheat sheet; Spring Academy "Building a REST API with Spring Boot" |
| Task 2 | OWASP JWT cheat sheet; jwt.io introduction; Spilcă on the Spring Security filter chain |
| Task 3 | OWASP Session Management cheat sheet; redis.io data-types tutorial + key naming conventions |
| Task 5 | Nothing new — money as `BigDecimal` is decided (ADR 0011); just apply it |
| Task 7 | PostgreSQL docs: partial indexes chapter (the one-active-tenancy constraint) + transaction isolation chapter (why the constraint beats check-then-act) |
| Task 8–9 | Nothing new — the charge/payment ledger model is decided (ADR 0006); Martin Fowler's "Money" pattern is optional background |
| Task 10 | redis.io caching patterns (cache-aside) |
| Task 11 | Nothing new — reuse work-honed Flutter knowledge, note deviations in conventions/flutter.md |

## Kotlin + Spring Boot

- **spring.io guide — "Building web applications with Spring Boot and Kotlin"** — official, purpose-built for exactly this stack.
- **Spring Framework docs — Kotlin support section** — the idioms Spring expects from Kotlin (open classes, constructor injection, null-safety at the boundary).
- **Spring Academy** (academy.spring.io) — free official courses; "Building a REST API with Spring Boot" is the Task-1-adjacent primer.
- **kotlinlang.org** — the language tour; the "Kotlin for Java developers" track if that's the entry angle.
- **"Spring Security in Action"** — Laurențiu Spilcă. Best deep treatment of the filter chain, which Tasks 2–3 have you hand-building. His **"Spring Start Here"** is the gentler general Spring intro.
- **Baeldung** — de-facto Spring cookbook for point lookups. Quality varies; cross-check against official docs.

## Auth / JWT

- **OWASP Cheat Sheet Series** (cheatsheetseries.owasp.org) — the *JWT*, *Session Management*, and *Password Storage* cheat sheets map directly onto Tasks 1–3. Short, authoritative.
- **jwt.io introduction** — token anatomy (header/payload/signature).

## PostgreSQL

- **Official docs — partial indexes chapter** — literally the Task 7 one-active-tenancy constraint. Read before that task, not during.
- **Official docs — transaction isolation chapter** — why the database-constraint approach beats an application check-then-act under concurrency.

## Money handling

- **Martin Fowler — "Money" pattern** (in *Patterns of Enterprise Application Architecture*, summarized free online) — why money is minor-unit integers or a decimal type, never a float. Background for ADR 0011.

## Redis

- **redis.io official docs** — data-types tutorial; the caching-patterns page (cache-aside is documented there by name); key naming conventions.
- **Redis University** — free official courses; RU101 covers everything this project needs.

## Modular monolith / architecture

- **Kamil Grzybek — "Modular Monolith: A Primer"** — canonical free write-up of the pattern; .NET-flavored, concept-transferable.
- **Simon Brown — "Modular Monoliths" talk** (YouTube) — origin of most current usage of the term.
- **Spring Modulith docs** — even if not adopted, articulates what module boundaries in a Spring monolith should look like. Adopting or rejecting it is itself an ADR.
- **ADR practice** — adr.github.io (includes MADR); Michael Nygard's original 2011 post "Documenting Architecture Decisions".

## REST API design

- **Zalando RESTful API Guidelines** — best public real-world API standards doc. Skim; steal what fits into conventions/kotlin-spring.md.

## Interview-prep crossover

- **"Designing Data-Intensive Applications"** — Martin Kleppmann. Chapter 7 (Transactions) directly deepens the one-active-tenancy constraint work; the single highest-value book for system-design interviews generally.
