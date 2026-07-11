# Conventions — Kotlin + Spring Boot

> **Draft, updated for the operations pivot (2026-07-12, ADR 0004).** Written
> mostly before the corresponding code exists, as a starting illustration
> rather than a decision reached while building. Per `docs/path.md`, conventions
> are meant to be recorded (or confirmed) when the first real choice comes up —
> revisit each section once the matching module is actually built. Sections that
> depend on still-open design decisions (money representation, how rent-owed is
> modelled, whether tenants log in — see the operations spec) are marked as such.

This doc is both a **rulebook** (what's consistent across the codebase) and a
**how-to** (where each kind of thing goes and what shape it takes). It describes
the artifacts in prose; you write the actual code.

---

## 1. Folder / package structure

The backend is a **modular monolith**: one deployable app, split into modules
with enforced boundaries (ADR 0002). Two levels of structure — *by module* at
the top, then *by layer* inside each module.

### Top level — by module

```
com.boardinghouse
├── auth          // users, login, JWT, roles
├── properties    // a house belongs to one owner
├── rooms         // rentable units under a property
├── tenants       // tenants and tenancies (who lives where, over what period)
├── billing       // rent charges, payments, balances — the operations core
└── shared        // cross-cutting: security config, error handling, Redis
```

A module owns exactly one area of the domain and one main entity (except
`shared`, which owns none). Nothing outside a module reaches into its internals
— cross-module calls go through a narrow facade (see §7).

### Inside a module — by layer

```
com.boardinghouse.<module>
├── web           // controllers, request/response DTOs, mappers
├── service       // business logic, ownership checks, transactions
└── persistence   // entities, repositories
```

Same three-layer shape in every module, so moving between modules never means
relearning a structure. The dependency direction inside a module is always
**web → service → persistence**, never the reverse: a controller may call a
service, a service may call a repository, but a repository never reaches back up
to a service, and an entity never knows about a DTO.

---

## 2. Naming conventions

| Thing | Convention | Example |
|---|---|---|
| Module package | lowercase noun | `billing` |
| Entity | singular noun | `Room`, `Tenancy`, `Charge` |
| Repository | `<Entity>Repository` | `RoomRepository` |
| Service | `<Area>Service` | `TenancyService` |
| Controller | `<Resource>Controller` | `RoomController` |
| Request DTO | `<Action><Noun>Request` | `CreateRoomRequest` |
| Response DTO | `<Noun>Response` | `RoomResponse` |
| Mapper functions | `toResponse()` / `toEntity()` | extension functions |
| Custom exception | `<Reason>Exception` | `NotFoundException` |

Resource paths are **plural nouns** (`/rooms`, `/tenancies`, `/charges`). One
file per top-level type; a small request/response DTO pair may share a file with
the controller it serves if it's only used there.

---

## 3. Models / Entities  → `persistence`

An entity is the persisted shape of one domain thing. Rules:

- **Location:** `persistence`, never exposed outside the module directly.
- **Identity:** a generated numeric id; the entity is created without one and
  the database assigns it on save.
- **No stored derived state.** The clearest example: a `Room` never stores an
  "occupied" flag — occupancy is *derived* by asking whether an active tenancy
  exists. Storing it would create a second source of truth that drifts. If a
  value can be computed from other records, compute it; don't persist it.
- **No cross-entity denormalization** for convenience — e.g. a `Room` does not
  carry a `currentTenantId`; the tenant↔room link lives on the `Tenancy`, which
  has its own start/end.
- **Nullability means "genuinely optional".** A tenancy's move-out date is
  nullable (empty while ongoing); most fields are non-null. Don't use nullable
  as a shortcut for "I'll fill it in later."
- **Money fields — decision pending.** How amounts (rent, payments) are typed
  is Open Decision #2 in the operations spec (integer minor units vs. a decimal
  type — never floating point). Do not settle it inline in an entity; make the
  call, write the ADR, then apply it everywhere at once.
- **JPA specifics:** entities need a no-arg-constructable, non-final shape,
  which the `kotlin("plugin.jpa")` + `allOpen` config in `build.gradle.kts`
  already provides for `@Entity`/`@Embeddable`/`@MappedSuperclass`. You write a
  normal Kotlin class; the plugin handles the bytecode Hibernate needs.

Entities assume their data is already valid — validation happens earlier, on the
request DTO (§4).

---

## 4. DTOs  → `web`

DTOs are the shapes that cross the HTTP boundary. **Entities never do** — not in
requests, not in responses. Every endpoint has its own DTOs.

- **Request DTOs** carry exactly the fields a client is allowed to send. This is
  a security boundary as much as a shape: the owner of a property is taken from
  the authenticated identity, **never** from a request field, so ownership is
  simply not a field on any request DTO. Bean Validation annotations
  (e.g. not-blank, positive amount) live here.
- **Response DTOs** carry exactly what the client should see — which may be
  *more* than the entity (a `RoomResponse` can include derived occupancy and the
  current tenant's name, stitched in at read time) or *less* (never leak a
  password hash, internal flags, etc.).
- **Mapping** is done by small explicit extension functions (`toResponse()`,
  `toEntity()`) — not a general-purpose mapping library. The mappings are simple
  enough that hand-written ones are clearer and keep the entity↔DTO boundary
  visible.

The rule of thumb: if you're tempted to return an entity "to save time," don't —
the DTO is what lets the stored shape and the API shape evolve independently.

---

## 5. Controllers  → `web`

Controllers are **thin**. Their only job is HTTP ⇄ method-call translation.

- Annotated `@RestController`, mapped to a plural resource path.
- Constructor-inject the one service they delegate to (§8).
- One function per endpoint. Each: accept a request DTO (or path/query params),
  call a single service method, return a response DTO.
- **No business logic, no ownership checks, no repository access** in a
  controller. If a controller is deciding anything beyond "which service method
  and which status code," that logic belongs in the service.
- **Status codes:** `201` for creation, `200` for reads/updates, `204` for
  actions with no body (delist, cancel, end).
- **No try/catch.** Failures are thrown as exceptions and handled centrally
  (§9); a controller never maps an exception to a status itself.

---

## 6. Services  → `service`

Services hold the business logic — the layer worth the most care.

- Constructor-inject the repositories (and any cross-module facades) they need.
- **Ownership and role checks live here**, not in controllers. This is where the
  ownership-chain walk happens: for a room action, load the room, walk room →
  property → owner, and compare against the authenticated user — returning the
  right 403-vs-404 distinction (§9).
- **Transaction boundaries are at the service method.** A method that changes
  data runs in one transaction; annotate at this layer, not in controllers or
  repositories.
- Services throw domain exceptions (`NotFoundException`, `ForbiddenException`,
  …) rather than returning error codes or nulls-as-errors.
- Services return entities or plain domain results to the `web` layer, which
  maps them to DTOs. A service does not build HTTP responses.

---

## 7. Repositories & cross-module access  → `persistence`

- **Repositories** are Spring Data JPA interfaces in `persistence`. Prefer
  derived query methods (`findByX`) for simple cases; a hand-written query for
  anything more involved. A repository is an implementation detail of its
  module.
- **A module never touches another module's repository or entities directly.**
  When `rooms` needs to know an owner, or `billing` needs to know an active
  tenancy, it calls a small **facade** the other module exposes — a single
  method with a narrow signature (e.g. "give me the owner id for this room",
  "is there an active tenancy for this room") — not the repository, not the raw
  entity. This is what keeps the module boundaries real; see
  `docs/architecture.md` for the allowed edges.

---

## 8. Dependency injection

**Constructor injection only.** Field injection (`@Autowired` on a
`lateinit var`) hides a class's real dependencies from anyone reading its
declaration and makes it awkward to construct outside the Spring container.
Kotlin's primary-constructor syntax makes constructor injection close to free —
list the dependencies as constructor parameters and let Spring wire them.

---

## 9. Error handling  → `shared`

One central `@RestControllerAdvice` in `shared` translates exceptions to HTTP
responses — never per-controller try/catch. A small set of custom exception
types maps to a consistent status scheme:

| Status | Meaning | Raised when |
|---|---|---|
| `400` | Bad input | request DTO validation fails |
| `401` | Not authenticated | missing / invalid / expired credentials |
| `403` | Not yours | valid credentials, but the resource belongs to someone else |
| `404` | Doesn't exist | the id matches nothing at all |
| `409` | Conflict | a database constraint rejects the change (e.g. assigning a tenant to a room that a uniqueness rule says is already full) |

The 401/403/404 three-way distinction is the important one and is documented
once in `docs/api.md`; this file just names where it's enforced (services throw,
the advice translates).

---

## 10. Kotlin style choices

- **Data classes** for all DTOs.
- **Nullability** reserved for genuinely optional fields (a tenancy's move-out
  date); everything else is non-null at the entity boundary.
- **`!!` is avoided** — a non-null assertion usually means the type should have
  been non-null, or the absence should have been handled, further up.
- **Expression-bodied functions** for simple one-line mappers and computations.
- Constructor parameters, one per line, trailing comma — so diffs stay small
  when a field is added.

---

## Related

- `docs/architecture.md` — the modules and the allowed dependency edges between
  them (the rules §7 enforces).
- `docs/api.md` — endpoint contracts and the error-shape detail behind §9.
- Operations spec (`second-brain: specs/2026-07-12-boarding-house-operations-app-design.md`)
  — the open design decisions that §3's money/derived-state notes defer to.
