package org.spring.soap2rest.soap.impl.model.db;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Mock for db table.
 */
public class AuthUser {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "service")
    private String service;

    @Column(name = "username")
    private String username;

    @Column(name = "passphrase")
    private String passphrase;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
