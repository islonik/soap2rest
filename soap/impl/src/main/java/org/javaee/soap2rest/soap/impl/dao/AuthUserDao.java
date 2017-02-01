package org.javaee.soap2rest.soap.impl.dao;

import org.javaee.soap2rest.soap.impl.model.AuthUser;

import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikilipa on 8/13/16.
 */
@Dependent
public class AuthUserDao {

    /**
     * Content of db table.
     */
    private static final List<AuthUser> users = new ArrayList() {
        {
            add(new AuthUser() {{
                setUser("restadmin");
                setPass("restadmin");
            }});
        }
    };

    public AuthUser getUser2Rest() {
        return users.get(0);
    }
}
