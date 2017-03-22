package org.spring.soap2rest.soap.impl.dao;

import org.spring.soap2rest.soap.impl.model.db.AuthUser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikilipa on 8/13/16.
 */
@Repository
public class AuthUserDao {

    /**
     * Content of db table.
     */
    private static final List<AuthUser> users = new ArrayList<AuthUser>() {
        {
            add(new AuthUser() {{
                setUsername("restadmin");
                setPassphrase("restadmin");
            }});
        }
    };

    public AuthUser getUser2Rest() {
        return users.get(0);
    }
}
