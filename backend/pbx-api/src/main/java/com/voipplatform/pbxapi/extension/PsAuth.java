package com.voipplatform.pbxapi.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

/**
 * SIP digest credentials. auth_type is a PostgreSQL enum.
 *
 * The password is stored in plain text because SIP digest authentication
 * requires the server to know the shared secret - it cannot be hashed the way
 * a login password would be. See docs/backend.md.
 */
@Entity
@Table(name = "ps_auths")
public class PsAuth {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "auth_type")
    private String authType;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    protected PsAuth() {
    }

    public PsAuth(String id, String authType, String username, String password) {
        this.id = id;
        this.authType = authType;
        this.username = username;
        this.password = password;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}