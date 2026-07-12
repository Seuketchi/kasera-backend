# 0011 — Money represented as BigDecimal

**Status:** Accepted
**Date:** 2026-07-12
**Supersedes:** ADR 0005 (integer minor units)

## Context

ADR 0005 chose integer minor units (centavos in a `Long`) for money. That is
exact, but it forces a `Cents` suffix on every money field and a mental ÷100 at
every boundary (`450000` means ₱4,500.00), which the builder found unintuitive to
read and work with. The actual requirement money has is **exactness** — it must
never drift — not specifically "minor units." A decimal type meets the exactness
requirement while reading naturally.

## Considered Options

- **Integer minor units (`Long` centavos)** — ADR 0005's choice. Exact; but every
  field carries a `Cents` suffix, amounts are stored as `450000`, and every
  display/input boundary divides/multiplies by 100.
- **`Double` / `Float`** — rejected outright: floating point cannot represent most
  decimal money values exactly and accumulates error under addition. Never for
  money.
- **`BigDecimal`** — exact decimal arithmetic; stores the real amount (`4500.00`)
  so field names are just `monthlyRent`, `deposit`, `amount` (no suffix, no ÷100);
  maps to SQL `NUMERIC`/`DECIMAL`, the standard money column type. Con: it's an
  object with explicit scale/rounding, and you must use `BigDecimal` math methods
  (`.add()`, `.compareTo()`), not `==`.

## Decision

We will represent all money as **`java.math.BigDecimal`**, superseding ADR 0005.
Money fields are named for what they hold — `monthlyRent`, `deposit`, `amount` —
with no `Cents` suffix, and store the actual peso value (e.g. `4500.00`). Hibernate
maps them to SQL `NUMERIC`. Amounts are still exact — `BigDecimal` never drifts —
so balances reconcile as before. Floating-point (`Double`/`Float`) remains
forbidden for money.

## Consequences

Easier: field names read naturally and there is no ÷100 conversion at any
boundary; the API sends/receives normal decimals (`4500.00`) instead of integers.

Harder: money math must use `BigDecimal` methods — `a.add(b)`, `a.subtract(b)`,
and `a.compareTo(b) == 0` for equality (never `==`, which compares object identity
/ scale). A consistent scale (2 decimal places) and rounding mode should be
applied when values are computed. Existing code that used `Long` cents
(`Room.monthlyRent: Long`) changes type to `BigDecimal`; the docs that said
"centavos" are updated to match.
