# 0006 — Rent owed modelled as generated periodic charges

**Status:** Accepted
**Date:** 2026-07-12

## Context

The core of an operations app is answering "who owes what." There are two
fundamentally different ways to model rent owed, and the choice shapes the
billing module, the payment model, and every balance query — so it has to be
decided before the `billing` module is designed.

## Considered Options

- **Compute owed on the fly** — store only the tenancy (rent + start/end) and the
  payments, and calculate what's owed at read time as "months elapsed × rent −
  payments." Pro: no charge records to generate or store; con: there is no
  explicit record of *what was billed when*, partial months and mid-tenancy rent
  changes become awkward special cases inside the query, and a payment can't be
  attached to a specific period it settles. The ledger is implicit and fragile.
- **Generate a charge record per period** — on each billing period, create a
  `Charge` row for the tenancy capturing the amount owed for that period and its
  due date. Payments are recorded against charges. Pro: an explicit, auditable
  ledger; "who owes what" is a straightforward sum over unpaid charge balances;
  each payment settles a known charge; history is preserved even if rent later
  changes. Con: charges must be generated (a deliberate action per period), and
  there are now more rows.

## Decision

We will model rent owed as **explicit `Charge` records generated per billing
period** (monthly). Each charge captures the amount owed (in centavos, per ADR
0005) and a due date at generation time; payments (ADR-independent) are recorded
against a specific charge. A tenant's balance is the sum of the outstanding
amounts of their unpaid/partially-paid charges.

Charge generation is an explicit operation for the MVP (an owner action or a
simple endpoint), not an automated scheduler — scheduling is a later-phase
concern and is out of scope here.

## Consequences

Easier: the ledger is explicit and auditable; balances are simple aggregations;
payments map cleanly to the period they pay; a rent change affects only
future charges, leaving past ones intact (the same price-lock-in reasoning a
lease follows).

Harder: charges have to be generated, so "no charge exists yet for this month"
is a real state to handle; there are more rows than the compute-on-the-fly model.
Proration of partial first/last months is deliberately deferred — MVP charges
whole periods; see the operations spec's deferred list.
