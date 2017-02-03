package org.spring.soap2rest.soap.impl.services;

import org.spring.soap2rest.soap.impl.dao.AuthUserDao;
import org.spring.soap2rest.soap.impl.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by nikilipa on 8/13/16.
 */
@Repository
public class DbAuthServices {

    @Autowired
    private AuthUserDao authUserDao;

    public AuthUser getUser() {
        return authUserDao.getUser2Rest();
    }

}
