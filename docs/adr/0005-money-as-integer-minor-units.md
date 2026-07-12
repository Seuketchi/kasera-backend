# 0005 — Money stored as integer minor units (centavos)

**Status:** Superseded by ADR 0011
**Date:** 2026-07-12

## Context

The operations app handles money in several places: a room's monthly rent, a
tenancy's locked-in rent and deposit, each rent charge, and each payment. How
money is represented in code and in the database has to be decided once, before
any money-bearing entity is written, because changing it later touches every one
of them. Floating-point types (`Float`/`Double`) cannot represent most decimal
money values exactly and accumulate rounding error under addition — unacceptable
for balances that must reconcile to the centavo.

## Considered Options

- **Floating point (`Double`)** — pro: trivial to write; con: inexact by
  construction, sums drift, and "why is the balance off by one centavo" is a
  classic, avoidable bug. Rejected outright for money.
- **Decimal type (`BigDecimal`)** — pro: exact decimal arithmetic, models
  currency naturally, scales to any precision; con: more ceremony (scale/rounding
  mode must be set deliberately), and it's an object, not a primitive.
- **Integer minor units (centavos as a `Long`)** — pro: exact, simple to reason
  about (a balance is just integer subtraction), impossible to end up with
  fractional centavos, and the standard representation in payment systems; con:
  formatting for display (÷100, insert decimal) happens at the edges, and you
  must remember every stored amount is centavos, not pesos.

## Decision

We will represent all money as **integer centavos in a `Long`** — the number of
₱0.01 units. `₱4,500.00` monthly rent is stored and computed as `450000`.
Conversion to a human-readable peso string happens only at the presentation edge
(in a response DTO or the client), never in storage or business logic.

## Consequences

Easier: all money arithmetic is exact integer math; balances always reconcile;
no rounding-mode decisions scattered through the code.

Harder: every developer (i.e. you) must keep in mind that a money field is
centavos — a field is named or documented to make that unmistakable, and the
peso/centavo conversion lives in exactly one place per boundary. Amounts entered
by a user in pesos are converted to centavos on the way in, at the request-DTO
boundary.
