# Dialplan reference

## Contexts

Building blocks (never assigned to an endpoint directly):

| Context | Purpose |
|---|---|
| `diagnostics` | Audio and signalling test extensions |
| `feature-codes` | Star codes for user-facing features |
| `internal-extensions` | Extension dialling and failover to voicemail |

Assigned contexts:

| Context | Includes | Reach |
|---|---|---|
| `from-internal` | diagnostics, feature-codes, internal-extensions | Full internal |
| `from-guest` | diagnostics | Test extensions only |

Verify effective reach at runtime:

``
dialplan show from-internal
``

## Extensions

| Number | Behaviour |
|---|---|
| `1000`–`1999` | Dial the matching PJSIP endpoint, 20s ring, failover to voicemail |
| `600` | Echo test — audio in both directions |
| `601` | Playback test — audio from PBX only |
| `602` | Reads the caller's own extension back |
| `*97` | Voicemail for the calling extension |
| `*98` | Voicemail, prompts for mailbox number |

`600` and `601` together isolate the direction of a one-way audio fault: if
`601` plays but `600` does not echo, inbound RTP to the PBX is broken.

## Call failover

`Dial()` sets `${DIALSTATUS}` on return. Because a bridged call never returns to
the next priority, any line after `Dial()` is a failure path.

## Voicemail

Mailboxes are defined in `voicemail.conf` under the `default` context. Messages
persist in the `asterisk-spool` Docker volume, mounted at `/var/spool/asterisk`.

Default PIN for local development is `1234`. Mailbox provisioning moves to the
control-plane API in a later phase.

## Reloading

Changes to mounted config files take effect without restarting the container:

```
dialplan reload
voicemail reload
pjsip reload
```

## IVR greeting

The main menu (`700`) plays `custom/ivr-greeting.wav` if present, and falls back
to the stock `demo-instruct` prompt if not. The check uses `STAT()` and is
evaluated per call, so changes take effect without a reload.

| Action | How |
|---|---|
| Record or re-record | Dial `*90`, speak, press `#` |
| Restore stock prompt | Delete `infra/asterisk/sounds/custom/ivr-greeting.wav` |
| Archive a version | Rename with a `.bak` suffix |

Custom audio must be 8 kHz mono 16-bit PCM to match the ulaw/alaw sample rate:

```bash
ffmpeg -i input.mp3 -ar 8000 -ac 1 -acodec pcm_s16le output.wav
```

Recorded greetings are gitignored — they are user-generated content, and third
party audio carries licensing obligations unsuitable for a public repository.