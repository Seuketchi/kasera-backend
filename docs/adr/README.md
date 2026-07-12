# Architecture Decision Records

One file per decision, numbered sequentially. Accepted ADRs are immutable —
to change a decision, write a new ADR that supersedes the old one and update
the old one's status. Format: see `0000-adr-template.md`.

| ADR | Title | Status |
|-----|-------|--------|
|0001|Separate repositories for backend and client|Accepted|
|0002|Modular monolith over microservices|Accepted|
|0003|No automated testing|Accepted|
|0004|Pivot from booking marketplace to operations/management app|Accepted|
|0005|Money stored as integer minor units (centavos)|Superseded by 0011|
|0006|Rent owed modelled as generated periodic charges|Accepted|
|0007|Room occupancy derived from an active tenancy, never stored|Accepted|
|0008|Owner-only MVP: tenants do not log in|Accepted|
|0009|One active tenancy per room, enforced by a database constraint|Accepted|
|0010|Deactivate instead of hard-delete|Accepted|
|0011|Money represented as BigDecimal (supersedes 0005)|Accepted|
