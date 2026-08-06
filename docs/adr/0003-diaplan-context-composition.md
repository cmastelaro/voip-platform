# 3. Compose dialplan from single-purpose contexts

Date: 2026-08-06

## Status

Accepted

## Context

In Asterisk, an endpoint's `context` determines which extensions it can reach.
A context can only reach extensions defined within it or within contexts it
includes. This is not a routing convenience layered over a separate permission
system — the context graph *is* the authorisation model.

The failure mode this guards against is toll fraud, the dominant financial risk
in VoIP. The typical incident: a weak endpoint password is brute-forced, the
attacker registers, and dials premium-rate international destinations. Losses
run to tens of thousands within a weekend. The credential is the breach; the
*loss* comes from that endpoint's context being able to reach an outbound route.

A single flat context containing every extension makes this risk invisible —
there is no artefact that states what a given endpoint may reach.

## Decision

Define small, single-purpose contexts that are never assigned to an endpoint
directly, and compose them with `include =>` into named contexts that are:

- `diagnostics` — echo, playback, caller-ID readback
- `feature-codes` — voicemail access
- `internal-extensions` — extension-to-extension dialling and failover

Assigned contexts:

- `from-internal` = diagnostics + feature-codes + internal-extensions
- `from-guest` = diagnostics only

An endpoint's reach is then readable as a single line, and verifiable at runtime
with `dialplan show <context>`.

## Consequences

- Adding outbound/PSTN routing later means creating an `outbound-national` or
  `outbound-international` building block and granting it per endpoint, rather
  than editing a shared context. Privilege is additive and explicit.
- `from-guest` currently has no endpoints assigned. It exists to demonstrate and
  test the boundary, and is the intended context for untrusted or unauthenticated
  peers in later phases.
- Slightly more indirection to read than a flat dialplan. Mitigated by
  `dialplan show`, which resolves includes and prints the effective reach.