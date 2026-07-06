# Conventions — Flutter

> **Draft, not yet earned.** Written before Task 11 exists in code, as an
> illustration of a plausible lighter-weight structure. This is the one file
> most likely to change once actually building it — real-world Flutter
> instincts (from work) will surface things this draft didn't anticipate.

## Project structure

By-feature folders, not by-layer: `auth/`, `properties/`, `rooms/`,
`bookings/`, each holding its own screens, small widgets, and a single
service/repository file for that feature's API calls. No separate
`data`/`domain`/`presentation` layering — this is a solo learning app, and
that ceremony pays off on larger teams, not here.

## State management

Plain `setState` for screen-local state; a single lightweight
`ChangeNotifier` (via `provider`) for state genuinely shared across screens
(e.g. the logged-in user). The line not to cross without revisiting this
doc: if a third cross-screen concern shows up needing shared state, that's
the signal to reconsider rather than bolting on another ad-hoc notifier.

## API client

A single `Dio` instance, base URL pointed at the local backend, built once
in an `api/` folder. An interceptor attaches the access token to every
outgoing request and, on a `401`, attempts one refresh-and-retry before
giving up. Tokens live in `flutter_secure_storage` — never
`SharedPreferences`, which is unencrypted local storage unsuitable for
credentials.

## Screens ↔ backend mapping

| Screen | Endpoint(s) | Notes |
|---|---|---|
| _(fill in during Tasks 11–13)_ | | |
