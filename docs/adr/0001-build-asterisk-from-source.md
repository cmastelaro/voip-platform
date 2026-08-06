# 1. Build Asterisk from source rather than install from a distribution package

Date: 2026-08-06

## Status

Accepted

## Context

The PBX layer needs Asterisk. The obvious approach is `apt-get install asterisk`
on a Debian base image.

This does not work. Asterisk was removed from Debian 12 (bookworm) in 2023 over
a dispute about maintainer capacity to commit to stable security updates, and it
has not returned to a Debian stable release since — it is absent from Debian 13
(trixie) as well, and exists only in unstable/sid. `apt-get install asterisk` on
any Debian stable release fails with "no installation candidate".

Options considered:

1. **Third-party community Docker image** — fastest, but unpinned provenance,
   unknown module selection, and no control over the compile-time flags that
   later phases depend on.
2. **Sangoma/FreePBX package repository** — maintained and current, but couples
   the project to the FreePBX ecosystem, which brings a PHP web stack this
   project explicitly replaces with its own control plane.
3. **Compile from source** — slower builds, more Dockerfile surface area, full
   control.

## Decision

Compile Asterisk 22 LTS from the official release tarball inside the container
image.

Compile-time choices that matter:

- `--with-pjproject-bundled` — builds the PJSIP version Asterisk was tested
  against, rather than whatever the distribution ships. Mismatched pjproject
  versions are a common source of subtle SIP behaviour differences.
- `--disable BUILD_NATIVE` — prevents the compiler from emitting instructions
  specific to the build machine's CPU, so the image is portable.
- ODBC modules (`res_odbc`, `res_config_odbc`) are compiled in because Phase 3
  moves endpoint configuration into PostgreSQL via Asterisk Realtime. This is a
  compile-time dependency, not a runtime one, so it must be decided here.

## Consequences

**Positive**

- Version is explicit and pinned via a build argument; upgrades are a one-line
  change with a reproducible build.
- Module set is chosen deliberately and documented.
- Independent of distribution packaging politics.

**Negative**

- Cold image build takes 10–25 minutes.
- The image carries a full C toolchain, inflating its size and attack surface.
  Mitigated in the hardening phase by splitting into a multi-stage build that
  discards the build environment.