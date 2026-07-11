# Conventions — Redis

> **Draft (operations app).** The auth key shapes are firm intent for Task 3; the
> report-cache row is provisional until Task 10 (optional). Revisit each once
> built.

Redis has two roles in this app: a **token store** for auth (Task 3) and an
optional **cache** for the reports view (Task 10). It is *not* a source of truth
for any domain data.

## Key naming scheme

Prefix pattern: `boardinghouse:<domain>:<type>:<id>`.

| Key type | Shape | Notes |
|---|---|---|
| Refresh token | `boardinghouse:auth:refresh:<tokenValue>` | Token-as-key — the token value itself is the key, not the user id, so a lookup only succeeds if you present the exact token. |
| Session index | `boardinghouse:auth:session:<userId>` | Secondary index pointing at the user's current refresh token key, enabling single-session enforcement (a new login finds and invalidates the old token). |
| Report cache | `boardinghouse:report:cache:<propertyId>:<yearMonth>` | Optional (Task 10). A property's monthly report, cached under a key derived from the property and the month. |

## TTL policy

- **Refresh token / session index** — TTL matches the intended refresh-token
  lifetime (e.g. 7 days). No key without an explicit expiry.
- **Report cache** — short TTL (a few minutes), invalidation TTL-only rather than
  actively invalidated when a payment lands. A report a few minutes stale is
  acceptable for an owner's dashboard, and active invalidation would add
  complexity (tracking which cached reports a given payment affects) for a
  freshness guarantee this project doesn't need.

## What Redis is NOT used for

**The one-active-tenancy-per-room rule.** That stays in Postgres as a database
constraint — see **ADR 0009**, which rejects a Redis lock explicitly. Recorded
here on purpose, so a future "just add a lock" instinct gets redirected back to
that ADR first. Redis never holds money, balances, or occupancy — those are all
Postgres (or derived from it).
