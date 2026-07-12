# ORM & Persistence — a learning note

> A concept explainer, grounded in this project's own code (your `Room` and
> `Property` entities) and a real bug you hit. Not a spec — a note to learn from.
> Companion to `dependency-injection.md`.

## 1. The core idea: you write objects, the ORM writes SQL

You never typed `CREATE TABLE` or `INSERT INTO`, yet tables exist and rows get
saved. That's because JPA/Hibernate is an **ORM** — *Object-Relational Mapping*.
Its whole job:

> **You write Kotlin objects. The ORM translates them into SQL.**

You work with `Room`, `save()`, `findById()`. Hibernate turns those into
`CREATE TABLE`, `INSERT`, `SELECT` behind the scenes.

## 2. What you wrote → what Hibernate generated

Everything you built in Tasks 1–2 has a hidden SQL counterpart:

| What you wrote (Kotlin) | What Hibernate generated (SQL) |
|---|---|
| `@Entity class Room(...)` (+ `ddl-auto: update`) | `CREATE TABLE room (id BIGINT, label VARCHAR, active BOOLEAN, ...)` |
| `repository.save(room)` | `INSERT INTO room (...) VALUES (?, ?, ...)` |
| `repository.findById(id)` | `SELECT * FROM room WHERE id = ?` |
| `repository.findByActiveTrue()` | `SELECT * FROM room WHERE active = true` |
| `repository.findByPropertyIdAndActiveTrue(pid)` | `SELECT * FROM room WHERE property_id = ? AND active = true` |
| `room.active = false; save(room)` | `UPDATE room SET active = false WHERE id = ?` |

You *did* see this once — in the 500 error trace:
`insert into room (active, description, label, monthly_rent, property_id) values (?,?,?,?,?)`.
That INSERT was **Hibernate's**, not yours.

Also note the **naming translation**: Kotlin `propertyId` → SQL column
`property_id`, `monthlyRent` → `monthly_rent` (camelCase → snake_case), class
`Room` → table `room`.

## 3. `ddl-auto` — the thing that made your tables

`spring.jpa.hibernate.ddl-auto: update` tells Hibernate: *"at startup, look at my
`@Entity` classes and change the database schema to match."* That's why a
`room` table appeared the moment you added the `Room` entity — no `CREATE TABLE`
needed from you.

Values you'll meet:
| Value | Behavior |
|---|---|
| `none` | do nothing (production default) |
| `validate` | check entities match the schema, change nothing |
| `update` | **add** missing tables/columns (what you use in dev) |
| `create` / `create-drop` | wipe and recreate every start (test only) |

## 4. ⚠️ The trap you hit: `update` is *add-only*

`ddl-auto: update` **only adds. It never drops or renames.** When you renamed the
field `isActive` → `active`:
- Hibernate **added** a new `active` column ✅
- Hibernate **left** the old `is_active NOT NULL` column behind ❌

Every new insert filled `active` but not the orphaned `is_active`, which violated
its `NOT NULL` constraint → `500`. The fix was raw SQL:
`ALTER TABLE room DROP COLUMN is_active;`

**The lesson:** when you rename or remove an entity field with `ddl-auto: update`,
the old column lingers. During dev, reset with
`docker compose down -v && docker compose up -d` (wipes the DB; Hibernate
recreates it clean), or drop the stale column by hand.

## 5. Why work through an ORM at all?

- **Stay in Kotlin** — objects and methods, not SQL strings scattered in code.
- **Database-agnostic** — the same code targets Postgres, MySQL, etc.; Hibernate
  emits the right dialect.
- **Less boilerplate** — no hand-writing every INSERT/SELECT and manually mapping
  result rows back into objects.

## 6. The costs (be honest about them)

- **The SQL is hidden** — you don't see schema changes or the queries run. That's
  *exactly* why the orphaned column blindsided you.
- **"Magic" until it isn't** — when something breaks, you need to understand the
  SQL underneath anyway.
- **Easy to write slow queries** — e.g. the "N+1 query" problem (loading a list,
  then one query per item). You'll meet this later.
- **`ddl-auto` is not for production** — real apps manage schema with migrations
  (see §8).

## 7. When you *do* write SQL yourself

You're not fully insulated. You drop to SQL for:
- **Complex queries** Spring Data can't derive from a method name →
  `@Query("select r from Room r where ...")` on the repository.
- **Native queries** → `@Query(value = "SELECT ...", nativeQuery = true)`.
- **Migrations** → real `CREATE TABLE` / `ALTER TABLE` in versioned `.sql` files.

## 8. The production answer: migrations (Flyway)

`ddl-auto` is a dev convenience. Real projects use **migrations** — numbered,
committed `.sql` files that describe each schema change explicitly
(`V1__create_room.sql`, `V2__rename_is_active.sql`). Migrations handle renames
and drops that `ddl-auto: update` can't — the `ALTER TABLE ... DROP COLUMN` that
fixed you is exactly what a migration would contain. Adopting Flyway is a good
future ADR + task for this project.

## 9. See the SQL yourself (demystify it)

Make Hibernate print every query it runs — add to `application.yaml`:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```
Now every `save`/`find` prints its generated SQL in the console. Great for
learning what your object calls actually do (turn it off later — it's noisy).

## 10. The abstraction spectrum (where you sit)

```
Raw JDBC  ─────────  Exposed (roamate)  ─────────  JPA / Hibernate (you)
write ALL the SQL     Kotlin SQL-like DSL           write objects, SQL hidden
most control          middle ground                 least SQL, most "magic"
```
Roamate's Exposed keeps you closer to SQL; JPA hides it. Different points on the
dial — neither is "right."

## The one-liner

> **You've been writing database code the whole time — as Kotlin objects. The ORM
> translates them into SQL.** Your `@Entity class Room` *is* your `CREATE TABLE`.
> You only hand-write SQL for complex queries and migrations.

## Related

- `conventions/kotlin-spring.md` §3 (entities), §7 (repositories)
- `dependency-injection.md` (the other concept note)
- The bug that taught this: `ddl-auto` add-only, ADR-worthy → migrations/Flyway
