# 2. Use Asterisk as the media/application layer

Date: 2026-08-06

## Status

Accepted

## Context

SIP software divides into two categories that are frequently confused:

- **Proxies / registrars** (Kamailio, OpenSIPS) handle signalling only. They
  route SIP messages and step out of the path; RTP flows directly between
  endpoints. Because per-call work is minimal, a single node handles thousands
  of calls per second.
- **B2BUAs / media servers** (Asterisk, FreeSWITCH) terminate the inbound leg,
  originate a new outbound leg, and bridge them. They sit in the media path,
  which is what makes IVR, voicemail, recording, conferencing, and transcoding
  possible at all. Throughput is orders of magnitude lower.

This project needs application-layer telephony features — IVR, voicemail,
recording — so a proxy alone cannot satisfy the requirement. A proxy has no
media to play a prompt into.

## Decision

Use Asterisk as the media/application layer.

Chosen over FreeSWITCH primarily for **ARI** (Asterisk REST Interface): a REST
API plus a WebSocket event stream. The Java control plane consumes this with
ordinary HTTP and WebSocket clients. FreeSWITCH's equivalent, ESL, is a bespoke
socket protocol requiring a dedicated client implementation — more effort for
no additional insight into telephony itself.

Asterisk's Realtime architecture also allows endpoint configuration to be read
from PostgreSQL rather than flat files, which is the mechanism the provisioning
API depends on.

## Consequences

- Media path runs through Asterisk (`direct_media=no`), enabling recording and
  transcoding at the cost of throughput. Acceptable at this scale, and
  deliberate: it makes the media observable during development.
- Scale ceiling is hundreds of concurrent calls, not thousands. A Kamailio edge
  proxy is planned in the hardening phase to demonstrate the standard
  production topology, with Asterisk behind it as the application layer.
- `chan_sip` does not exist in Asterisk 21+. All SIP handling uses `chan_pjsip`
  and `pjsip.conf`. Much online documentation predates this and is unusable.