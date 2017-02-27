package org.javaee.soap2rest.soap.impl.model.sql;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "s2r_soap_auth")
@NamedQueries({
        @NamedQuery(name = "findAllUsers", query =
                "select authUser from AuthUser authUser"
        )
})
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
