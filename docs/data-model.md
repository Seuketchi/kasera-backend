# Data Model

<!-- Start during Task 1 (User), extend as each entity arrives:
     Property (Task 4), Room (Task 5), Booking (Task 6). -->

## Entities

<!-- Per entity: fields, types (in prose), and which module owns it.
     Call out the deliberate absences too — e.g. Room has NO stored
     availability flag, and why. -->

## Relationships

<!-- Who references whom, and the cardinality. A Mermaid ER diagram
     is worth doing here — it renders on GitHub and is a skill in
     itself. -->

## Booking lifecycle

<!-- The state table: state, meaning, reached-from. Then the two rules
     that hang off it:
     - what actually frees a room (leaving "approved and not-yet-ended",
       via ANY exit — cancel or end)
     - auto-reject of competing pending requests on approval -->

## Integrity rules enforced by the database

<!-- The partial-unique-constraint rule in prose: at most one booking
     per room may be approved with no end date. State WHY it lives at
     the database layer (link the ADR once written). Add other
     constraints (unique email, etc.) as they appear. -->
