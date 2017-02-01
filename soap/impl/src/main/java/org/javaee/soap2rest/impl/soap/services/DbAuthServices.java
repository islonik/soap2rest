package org.javaee.soap2rest.impl.soap.services;

import org.javaee.soap2rest.impl.soap.dao.AuthUserDao;
import org.javaee.soap2rest.impl.soap.model.AuthUser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Created by nikilipa on 8/13/16.
 */
@ApplicationScoped
public class DbAuthServices {

    @Inject
    private AuthUserDao authUserDao;

    public AuthUser getUser() {
        return authUserDao.getUser2Rest();
    }

}
