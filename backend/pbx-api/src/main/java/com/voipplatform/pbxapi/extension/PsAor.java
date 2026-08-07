package com.voipplatform.pbxapi.extension;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

/**
 * Address of Record: where an endpoint can be reached once registered.
 *
 * remove_existing is a PostgreSQL enum (yesno), not text. JdbcTypeCode(OTHER)
 * tells Hibernate to let PostgreSQL resolve the cast rather than binding it
 * as a varchar, which the database rejects.
 */
@Entity
@Table(name = "ps_aors")
public class PsAor {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "max_contacts")
    private Integer maxContacts;

    @Column(name = "remove_existing")
    private String removeExisting;

    @Column(name = "qualify_frequency")
    private Integer qualifyFrequency;

    protected PsAor() {
    }

    public PsAor(String id, Integer maxContacts, String removeExisting, Integer qualifyFrequency) {
        this.id = id;
        this.maxContacts = maxContacts;
        this.removeExisting = removeExisting;
        this.qualifyFrequency = qualifyFrequency;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getMaxContacts() { return maxContacts; }
    public void setMaxContacts(Integer maxContacts) { this.maxContacts = maxContacts; }

    public String getRemoveExisting() { return removeExisting; }
    public void setRemoveExisting(String removeExisting) { this.removeExisting = removeExisting; }

    public Integer getQualifyFrequency() { return qualifyFrequency; }
    public void setQualifyFrequency(Integer qualifyFrequency) { this.qualifyFrequency = qualifyFrequency; }
}