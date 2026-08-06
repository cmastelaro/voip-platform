-- Extension 1000 as realtime objects.
-- 1001 deliberately remains in pjsip.conf to demonstrate both
-- mechanisms serving calls simultaneously.

DELETE FROM ps_endpoints WHERE id = '1000';
DELETE FROM ps_auths     WHERE id = '1000';
DELETE FROM ps_aors      WHERE id = '1000';

INSERT INTO ps_aors (id, max_contacts, remove_existing, qualify_frequency)
VALUES ('1000', 2, 'yes', 30);

INSERT INTO ps_auths (id, auth_type, username, password)
VALUES ('1000', 'userpass', '1000', 'Str0ngPass1000');

INSERT INTO ps_endpoints (
    id, transport, aors, auth, context,
    disallow, allow,
    direct_media, force_rport, rewrite_contact,
    dtmf_mode, callerid
) VALUES (
    '1000', 'transport-udp', '1000', '1000', 'from-internal',
    'all', 'ulaw,alaw',
    'no', 'yes', 'yes',
    'rfc4733', 'Alice <1000>'
);