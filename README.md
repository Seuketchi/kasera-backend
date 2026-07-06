# Boarding House Booking — Backend

A booking system for boarding houses: owners list properties and rooms,
boarders request bookings, owners approve or reject them. This is a
deliberate learning project — every line is written by hand, decisions are
recorded as they're made, and the build order lives in [docs/path.md](docs/path.md).
Per [ADR 0001](docs/adr/0001-separate-repos-for-backend-and-client.md) the
Flutter client lives in its own repository; this repo is the backend and the
home of the shared documentation.

## Stack

- **Backend:** Kotlin / Spring Boot 4, Java 21 toolchain (Gradle downloads
  the JDK itself via the foojay resolver — no local JDK 21 install needed)
- **Database:** PostgreSQL 17, run via Docker
- **Cache / sessions:** Redis, run via Docker (arrives in Task 3 — not
  needed to boot the app today)
- **Client:** Flutter (separate repository)

## Running locally

Prerequisites: Docker. (Gradle and the JDK are handled by the wrapper.)

1. **Start Postgres:**

   ```bash
   docker run -d --name boardinghouse-db -e POSTGRES_DB=boardinghouse -e POSTGRES_USER=boardinghouse -e POSTGRES_PASSWORD=devpassword -p 5432:5432 postgres:17
   ```

   Verify the user/database actually exist (catches a mistyped `-e` flag
   immediately):

   ```bash
   docker exec boardinghouse-db psql -U boardinghouse -d boardinghouse -c 'select 1'
   ```

   To **reset** the database to factory-fresh, delete and recreate it:

   ```bash
   docker rm -f boardinghouse-db   # then re-run the docker run above
   ```

2. **Run the backend:**

   ```bash
   ./gradlew bootRun
   ```

   Gradle parks at "85% EXECUTING" while the app runs — that's normal; the
   task only "finishes" when the server stops. Look for
   `Started BackendApplication` in the log. Stop with Ctrl+C.

3. **Check it's alive:** `curl -i http://localhost:8080` should return
   **401 Unauthorized**. Until real auth lands (Task 2), Spring Security
   guards everything with a default login form — user `user`, password
   printed in the startup log (`Using generated security password: …`) —
   and logging in leads to a 404, because no endpoints exist yet. All of
   that is expected.

## Documentation

| Doc | What it covers |
|-----|----------------|
| [docs/path.md](docs/path.md) | How to walk this project — the per-task loop |
| [docs/architecture.md](docs/architecture.md) | Modules and dependency rules |
| [docs/data-model.md](docs/data-model.md) | Entities as they get built |
| [docs/api.md](docs/api.md) | Endpoint contracts and error shapes |
| [docs/conventions/](docs/conventions) | Kotlin/Spring, Redis, and Flutter conventions |
| [docs/adr/](docs/adr) | Decision records (index in its README) |
| [docs/learning-sources.md](docs/learning-sources.md) | Reading map per task |