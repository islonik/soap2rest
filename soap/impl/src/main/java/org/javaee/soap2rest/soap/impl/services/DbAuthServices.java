package org.javaee.soap2rest.soap.impl.services;

import org.javaee.soap2rest.soap.impl.dao.AuthUserDao;
import org.javaee.soap2rest.soap.impl.model.AuthUser;

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
