# 5. The control plane writes Asterisk's schema directly

Date: 2026-08-07

## Status

Accepted

## Context

The control plane provisions SIP extensions. Asterisk reads endpoint
configuration from PostgreSQL at call time (see
[ADR 0004](0004-pjsip-realtime-over-odbc.md)), so provisioning is a database
write.

Two shapes were available:

1. **Own domain model, synchronised to Asterisk.** The control plane keeps its
   own `extension` table and projects rows into `ps_endpoints`, `ps_auths` and
   `ps_aors`. Clean domain boundaries, but two sources of truth that can drift,
   and a synchronisation path to build and debug.
2. **Write Asterisk's tables directly.** The control plane treats the `ps_*`
   tables as its persistence layer.

## Decision

Write Asterisk's tables directly. `ps_endpoints`, `ps_auths` and `ps_aors` are
the single source of truth for endpoint configuration.

Consequences for how the backend is built:

- **Hibernate must never modify the schema.** `ddl-auto: none` is mandatory.
  The schema is owned by Asterisk's Alembic migrations; Hibernate "correcting" a
  table it does not understand would corrupt PBX configuration.
- **Entities map a subset of columns.** `ps_endpoints` has roughly 150 columns;
  only those the control plane uses are mapped. JPA ignores the remainder, and
  columns left unmapped retain their database defaults.
- **Creation spans three tables and must be transactional.** An endpoint without
  its matching auth and AOR fails at registration with a 500 and no clear cause.
- **Several columns are PostgreSQL enum types**, not text. Values are constrained
  at the database level, which catches invalid configuration on write rather than
  silently at load.

## Consequences

**Positive**

- No synchronisation layer, and no possibility of drift between the control
  plane's view and Asterisk's.
- A write is live on Asterisk's next lookup. No reload, no signal, no coupling to
  Asterisk's config file syntax.
- Registration state is readable from `ps_contacts` with an ordinary query.

**Negative**

- The control plane's persistence model is dictated by another project's schema,
  and Asterisk major upgrades may change it. Mitigated by mapping a deliberately
  narrow subset of columns.
- Domain concepts the control plane will eventually need — ownership, audit
  history, soft deletion — have nowhere to live in Asterisk's tables. These will
  require separate control-plane-owned tables alongside, in a separate schema, so
  the boundary stays explicit.