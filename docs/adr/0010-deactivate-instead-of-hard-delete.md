# 0010 — Deactivate instead of hard-delete

**Status:** Accepted
**Date:** 2026-07-12

## Context

Owners will stop renting out rooms and retire properties. The question is what
"removing" one means. Properties and rooms are referenced by history that must
not disappear: a room is referenced by past tenancies, which are referenced by
past charges and payments. Hard-deleting a room out from under that history would
either orphan or cascade-destroy financial records that need to be kept.

## Considered Options

- **Hard delete** — remove the row. Pro: the simplest mental model; con: destroys
  or orphans the tenancy/charge/payment history that references it; a
  past-tenant's ledger would lose the room it was for. Wrong for anything with
  financial history.
- **Deactivate (soft)** — mark the property or room inactive instead of removing
  it. Pro: history stays intact and referentially valid; an inactive room simply
  stops appearing in the owner's active lists and can't take a new tenancy; con:
  queries must filter on the active flag, and "deleted" now means two things
  (gone vs. inactive) that must be kept straight.

## Decision

We will **deactivate** properties and rooms — a boolean active flag — rather than
hard-delete them. Deactivating a property also hides its rooms from active
listings regardless of each room's own flag. A room with an active tenancy cannot
be deactivated until that tenancy has ended. Tenants and tenancies, likewise, are
ended (a move-out date), never deleted.

## Consequences

Easier: all financial and tenancy history stays intact and valid; nothing
cascades destructively; "undo" is just re-activating.

Harder: every "list active" query must filter on the flag, and it's on the
developer to remember that inactive rows still exist; the ordinary REST instinct
that `DELETE` erases a row is deliberately not honored here — a delete-style
action sets the flag instead. This is recorded so a future "just delete it"
change gets redirected back to this ADR.
