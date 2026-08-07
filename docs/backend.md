# Control plane (pbx-api)

Spring Boot service providing REST provisioning over Asterisk's realtime
configuration tables, and — from the ARI integration onward — live call control.

Located at `backend/pbx-api`.

## Stack

| | |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.x |
| Build | Maven (wrapper included) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 17 (shared with Asterisk) |

## Running

Requires PostgreSQL to be up and migrated. From `backend/pbx-api`:

```bash
cp .env.local.example .env.local     # then edit the credentials
source .env.local
./mvnw spring-boot:run
```

The service listens on port 8080. Asterisk's own HTTP server uses 8088.

## Configuration

All settings are placeholders with defaults, resolved from environment
variables. `application.yml` contains no credentials.

| Variable | Default | Purpose |
|---|---|---|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5433` | PostgreSQL port (5433 avoids a native install on 5432) |
| `DB_NAME` | `asterisk` | Database name |
| `DB_USER` | `asterisk` | Database user |
| `DB_PASSWORD` | *(none)* | No default — startup fails if unset |
| `ARI_HOST` | `localhost` | Asterisk ARI host |
| `ARI_PORT` | `8088` | Asterisk ARI port |
| `ARI_USER` | `voipapi` | ARI user from `ari.conf` |
| `ARI_PASSWORD` | *(none)* | No default |
| `ARI_APP` | `voip-platform` | Stasis application name |
| `SERVER_PORT` | `8080` | HTTP port |
| `SHOW_SQL` | `false` | Log generated SQL |

Credential variables have deliberately empty defaults. A working default
password in a committed file is a working default password in production.

Spring resolves configuration in this order, later winning:

1. `application.yml`
2. `application-{profile}.yml`
3. Environment variables
4. Command-line arguments

Relaxed binding means `SPRING_DATASOURCE_URL` maps to `spring.datasource.url`
without a placeholder — useful when running in a container.

`.env.local` and `application-local.yml` are gitignored.

## Schema ownership

The schema belongs to Asterisk's Alembic migrations. See
[ADR 0005](adr/0005-control-plane-writes-asterisk-schema.md).

**`ddl-auto: none` is not optional.** Any other value permits Hibernate to alter
tables it does not fully understand, which would corrupt PBX configuration.

Entities map only the columns the control plane uses. `ps_endpoints` has roughly
150; unmapped columns retain their database defaults.

## Package layout

```
com.voipplatform.pbxapi
└── extension
├── PsEndpoint.java entity, subset of ps_endpoints
├── PsEndpointRepository.java Spring Data interface
└── ExtensionController.java REST endpoints
```

Packaged by feature rather than by layer, so a feature's entity, repository and
controller sit together.

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/extensions` | List provisioned extensions |

```bash
curl -s http://localhost:8080/api/extensions | jq
```

Only extensions stored in PostgreSQL are returned. Extension `1001` remains
defined in `pjsip.conf` and is deliberately invisible to this API — the control
plane manages database-backed configuration only. See
[realtime configuration](realtime.md) for the two mechanisms running side by
side.

## Notes on Spring, for reference

**Repositories have no implementation.** `PsEndpointRepository` is an interface
with no body. Spring Data generates an implementing proxy at startup from the
interface and the entity mapping, supplying `findAll`, `findById`, `save`,
`deleteById` and others.

**Constructor injection needs no annotation.** A class with a single constructor
has its dependencies supplied by Spring automatically.

**`@RestController`** combines request routing with automatic JSON
serialisation of return values.