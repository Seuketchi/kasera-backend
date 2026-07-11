# Kasera — Boarding House Operations Backend

> **Kasera** (Filipino: *landlady / landlord*) — the owner's tool for running a
> boarding house. Formerly a booking marketplace; pivoted to an
> operations/management app on 2026-07-12
> ([ADR 0004](docs/adr/0004-pivot-from-booking-marketplace-to-operations-app.md)).

An operations tool a boarding-house **owner** uses to run the place: track who
lives in which room (tenants and tenancies), charge rent each month, record
payments, and see who owes what. Owner-only for the MVP — tenants are records the
owner manages, not accounts ([ADR 0008](docs/adr/0008-owner-only-tenants-do-not-log-in.md)).

This is a deliberate learning project — every line is written by hand, decisions
are recorded as ADRs, and the build order lives in [docs/path.md](docs/path.md).
Per [ADR 0001](docs/adr/0001-separate-repos-for-backend-and-client.md) the Flutter
client lives in its own repository; this repo is the backend and the home of the
shared documentation.

## Stack

- **Backend:** Kotlin / Spring Boot 4, Java 21 toolchain (Gradle downloads the
  JDK itself via the foojay resolver — no local JDK 21 install needed)
- **Database:** PostgreSQL, run via Docker
- **Cache / sessions:** Redis, run via Docker (used from Task 3 onward)
- **Client:** Flutter (separate repository)

## Running locally

Prerequisites: Docker. (Gradle and the JDK are handled by the wrapper.)

1. **Start Postgres + Redis:**

   ```bash
   docker compose up -d
   ```

   This uses `docker-compose.yml` — database `boardinghouse`, user `boardinghouse`,
   password `devpassword`, plus Redis. To reset the database to factory-fresh:
   `docker compose down -v` then `docker compose up -d`.

2. **Run the backend:**

   ```bash
   ./gradlew bootRun
   ```

   Gradle parks at "EXECUTING" while the app runs — that's normal; the task only
   "finishes" when the server stops. Look for `Started BackendApplicationKt` in
   the log. Stop with Ctrl+C.

3. **Check it's alive:** `curl -i http://localhost:8080` returns **401
   Unauthorized** until real auth lands (Task 2) — Spring Security guards
   everything with a default login (user `user`, password printed in the startup
   log as `Using generated security password: …`). That's expected.

### Hot reload (optional)

DevTools is on. For auto-restart on save, run the app in one terminal
(`./gradlew bootRun`) and a recompile watcher in another (`./gradlew -t classes`):
saving a `.kt` file recompiles it and DevTools restarts the app in ~1s.

## Documentation

| Doc | What it covers |
|-----|----------------|
| [docs/path.md](docs/path.md) | The task-by-task build order and the per-task loop |
| [docs/architecture.md](docs/architecture.md) | Modules and the allowed dependency edges |
| [docs/data-model.md](docs/data-model.md) | Entities, relationships, lifecycle, DB rules |
| [docs/api.md](docs/api.md) | Endpoint contracts and error shapes |
| [docs/conventions/](docs/conventions) | Kotlin/Spring, Redis, and Flutter conventions |
| [docs/adr/](docs/adr) | Decision records (index in its README) |
| [docs/learning-sources.md](docs/learning-sources.md) | Reading map per task |
