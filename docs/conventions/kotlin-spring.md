# Conventions — Kotlin + Spring Boot

> **Draft, not yet earned.** Written before any code exists, as a starting
> illustration rather than a decision made while building. Per `docs/path.md`,
> conventions are meant to be recorded when the first real choice comes up —
> rewrite (or just confirm) each section once the corresponding task is
> actually built, rather than trusting this draft at face value.

## Package layout inside a module

Each module (`auth`, `properties`, `rooms`, `bookings`, `shared`) is laid out
by layer internally:

```
com.boardinghouse.<module>
├── web          // controllers, request/response DTOs
├── service      // business logic
└── persistence  // JPA entities, repositories
```

Same layered shape in every module, so moving between them doesn't require
relearning a structure.

## Dependency injection

Constructor injection only. Field injection (`@Autowired` on a `var`/`lateinit`
property) hides a class's real dependencies from anyone reading its
declaration and makes it harder to construct outside the Spring container.
Kotlin's primary-constructor syntax makes constructor injection close to free.

## Entity vs DTO boundary

JPA entities never cross the controller boundary in either direction. Every
endpoint has its own request/response data classes in `web`, mapped to/from
entities by small explicit mapper functions (not a general-purpose mapping
library — the mappings are simple enough not to need one). Bean Validation
annotations live on the request DTOs; entities assume already-validated data.

## Error handling

One central `@RestControllerAdvice` translates exceptions to HTTP responses,
rather than per-controller try/catch. A small set of custom exception types
(e.g. `NotFoundException`, `ForbiddenException`) map to the three-way
401/403/404 distinction from `docs/api.md`, plus 400 for validation failures
and 409 for the booking-approval database-constraint conflict (Task 7).

## REST conventions

Plural-noun resource paths (`/properties`, `/rooms`, `/bookings`). Success
status codes: `201` for creation, `200` for reads/updates, `204` for
delist/cancel-style actions with no body to return. Borrowed from the Zalando
guidelines where it fit; see `docs/learning-sources.md`.

## Kotlin style choices

Data classes for all DTOs. Nullability reserved for fields that are
genuinely optional (e.g. `Booking.endDate: LocalDate?`); everything else is
non-null at the entity boundary. `!!` is avoided — a non-null assertion
usually means the type should have been non-null (or handled) in the first
place. Expression-bodied functions for simple one-line mappers/computations.
