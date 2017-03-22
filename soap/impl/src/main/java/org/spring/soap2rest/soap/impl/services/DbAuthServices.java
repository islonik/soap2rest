package org.spring.soap2rest.soap.impl.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.soap.impl.dao.AuthUserDao;
import org.spring.soap2rest.soap.impl.model.db.AuthUser;
import org.spring.soap2rest.soap.impl.services.db.SoapScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikilipa on 8/13/16.
 */
@Repository
public class DbAuthServices implements SoapScheduler {

    private static final Logger log = LoggerFactory.getLogger(DbAuthServices.class);

    private static Cache<String, AuthUser> soapAuthCache = CacheBuilder.newBuilder()
            .expireAfterWrite(EXPIRE_AFTER_WRITE_DURATION, TimeUnit.MINUTES)
            .build();

    private static final String DEFAULT = "default";

    @Autowired
    private AuthUserDao authUserDao;

    public AuthUser getUser(String service) {
        return Optional
                .ofNullable(soapAuthCache.getIfPresent(service))
                .orElseGet(() -> authUserDao.getUser2Rest());
    }

    @Override
    public void updateCache() {
        // TODO: implement
    }

}
