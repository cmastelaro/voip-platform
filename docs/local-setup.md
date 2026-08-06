# Local setup

## Requirements

- Linux host (containers use `network_mode: host`)
- Docker Engine 29+ with Compose v5+
- UDP 5060 free on the host

Verify the port is unoccupied before starting:

```bash
sudo ss -lunp | grep 5060
```

## Why host networking

SIP carries IP addresses and ports inside the SDP body of its messages. Docker's
bridge networking rewrites packet headers but not payloads, so the SDP continues
to advertise a container-internal address the far end cannot reach. The result is
signalling that succeeds and audio that silently goes nowhere — the single most
common and most confusing VoIP failure.

Host networking sidesteps this during development. The production answer is
STUN/TURN, addressed in the hardening phase.

## Start

```bash
cd infra
docker compose up -d --build
docker exec -it pbx asterisk -rvvv
```

First build compiles Asterisk from source and takes 10–25 minutes. Subsequent
builds are cached.

## Softphone configuration

Asterisk binds UDP 5060, so every softphone on the same host must use a
different local SIP port. Disable STUN and ICE — there is no NAT on loopback and
they only add misleading candidate addresses.

| | Extension 1000 | Extension 1001 |
|---|---|---|
| Username | `1000` | `1001` |
| Password | `Str0ngPass1000` | `Str0ngPass1001` |
| Domain | `127.0.0.1` | `127.0.0.1` |
| Local SIP port | `5062` | `5064` |

Credentials are development defaults committed deliberately. They are replaced
by generated secrets when provisioning moves to the control plane.

### baresip

```bash
sudo apt-get install -y baresip
mkdir -p ~/.baresip-1000 ~/.baresip-1001
timeout 3 baresip -f ~/.baresip-1000 >/dev/null 2>&1
timeout 3 baresip -f ~/.baresip-1001 >/dev/null 2>&1

echo '<sip:1000@127.0.0.1>;auth_user=1000;auth_pass=Str0ngPass1000;transport=udp;regint=300' \
  > ~/.baresip-1000/accounts
echo '<sip:1001@127.0.0.1>;auth_user=1001;auth_pass=Str0ngPass1001;transport=udp;regint=300' \
  > ~/.baresip-1001/accounts

sed -i 's|^#*\s*sip_listen.*|sip_listen 0.0.0.0:5062|' ~/.baresip-1000/config
sed -i 's|^#*\s*sip_listen.*|sip_listen 0.0.0.0:5064|' ~/.baresip-1001/config
```

Run each in its own terminal with `baresip -f ~/.baresip-1000`.

Keys: `d` dial, `a` answer, `b` hang up, `q` quit, `/callstat` for live RTP
counters.

## Verifying

```
pjsip show endpoints
pjsip show contacts
dialplan show from-internal
voicemail show users
```