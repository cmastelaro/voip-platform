package com.voipplatform.pbxapi.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maps a subset of Asterisk's ps_endpoints table.
 *
 * The real table has roughly 150 columns. Only the ones the control plane
 * uses are mapped; JPA ignores the rest. Schema ownership belongs to
 * Asterisk's Alembic migrations - Hibernate must never modify it.
 *
 * Enum-typed columns (direct_media, force_rport, dtmf_mode) are deliberately
 * omitted here. They need explicit handling and are only required on write.
 */
@Entity
@Table(name = "ps_endpoints")
public class PsEndpoint {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "transport")
    private String transport;

    @Column(name = "aors")
    private String aors;

    @Column(name = "auth")
    private String auth;

    @Column(name = "context")
    private String context;

    @Column(name = "disallow")
    private String disallow;

    @Column(name = "allow")
    private String allow;

    @Column(name = "callerid")
    private String callerid;

    @Column(name = "direct_media")
    private String directMedia;

    @Column(name = "force_rport")
    private String forceRport;

    @Column(name = "rewrite_contact")
    private String rewriteContact;

    protected PsEndpoint() {
        // required by JPA
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getAors() { return aors; }
    public void setAors(String aors) { this.aors = aors; }

    public String getAuth() { return auth; }
    public void setAuth(String auth) { this.auth = auth; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public String getDisallow() { return disallow; }
    public void setDisallow(String disallow) { this.disallow = disallow; }

    public String getAllow() { return allow; }
    public void setAllow(String allow) { this.allow = allow; }

    public String getCallerid() { return callerid; }
    public void setCallerid(String callerid) { this.callerid = callerid; }

    public String getDirectMedia() { return directMedia; }
    public void setDirectMedia(String directMedia) { this.directMedia = directMedia; }

    public String getForceReport() { return forceRport; }
    public void setForceReport(String forceRport) { this.forceRport = forceRport; }

    public String getRewriteContact() { return rewriteContact; }
    public void setRewriteContact(String rewriteContact) { this.rewriteContact = rewriteContact; }
}