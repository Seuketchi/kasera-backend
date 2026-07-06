# Conventions — Redis

> **Draft, not yet earned.** Written before Task 3 exists in code. Revisit
> once refresh tokens (Task 3) and search caching (Task 10) are actually
> built — the key shapes below are a plausible starting point, not a
> decision reached by building anything yet.

## Key naming scheme

Prefix pattern: `boardinghouse:<domain>:<type>:<id>`.

| Key type | Shape | Notes |
|---|---|---|
| Refresh token | `boardinghouse:auth:refresh:<tokenValue>` | Token-as-key — the token value itself is the key, not the user id, so a lookup only succeeds if you present the exact token. |
| Session index | `boardinghouse:auth:session:<userId>` | Secondary index pointing at the user's current refresh token key, enabling single-session enforcement (a new login can find and invalidate the old token). |
| Search cache | `boardinghouse:search:cache:<normalizedFilterHash>` | Filters (price min/max, location text) sorted into a canonical order and hashed, so the same logical search always produces the same key regardless of parameter order. |

## TTL policy

- **Refresh token / session index** — TTL matches the intended refresh-token
  lifetime (e.g. 7 days). No key without an explicit expiry.
- **Search cache** — short TTL (a few minutes). Per the spec, invalidation is
  TTL-only rather than actively invalidated on room/listing changes — search
  results don't need second-to-second freshness, and active invalidation
  would add complexity (tracking which cache keys a given room's filters
  could match) for a freshness guarantee this project doesn't need.

## What Redis is NOT used for

Booking-conflict prevention. That stays in Postgres as a database constraint
— see the (Task 7) ADR once written. Recorded here on purpose, so a future
"just add a lock" instinct gets redirected back to this file first.
