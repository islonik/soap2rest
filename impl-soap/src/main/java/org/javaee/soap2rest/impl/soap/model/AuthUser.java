package org.javaee.soap2rest.impl.soap.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.UUID;

/**
 * Mock for db table.
 */
public class AuthUser {
    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "login")
    private String user;

    @Column(name = "password")
    private String pass;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
