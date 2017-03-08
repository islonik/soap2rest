package org.spring.soap2rest.rest.web.model;

import java.util.*;

/**
 * Created by nikilipa on 3/8/17.
 */
public class User {

    private String name;
    private String password;
    private List<String> roles;

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String[] getRoles() {
        if (roles == null) {
            roles = Collections.emptyList();
        }
        return roles.stream().toArray(String[]::new);
    }

    public void setUpRoles(String roles) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
            StringTokenizer stringTokenizer = new StringTokenizer(roles, ",");
            while (stringTokenizer.hasMoreTokens()) {
                String role = stringTokenizer.nextToken();
                this.roles.add(role);
            }
        }
    }
}
