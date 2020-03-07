package org.javaee.soap2rest.soap.impl.services.sql;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.javaee.soap2rest.soap.impl.dao.AuthUserDao;
import org.javaee.soap2rest.soap.impl.model.sql.AuthUser;
import org.javaee.soap2rest.utils.liquibase.annotations.Liquibase;
import org.javaee.soap2rest.utils.liquibase.annotations.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikilipa on 8/13/16.
 */
@ApplicationScoped
@Default
@Liquibase
@Schema(name = "s2r_soap_auth", resource = "/liquibase/auth_changelog.xml")
public class DbAuthServices implements SoapScheduler {

    private static final Logger log = LoggerFactory.getLogger(DbAuthServices.class);

    private static Cache<String, AuthUser> soapAuthCache = CacheBuilder.newBuilder()
            .expireAfterWrite(EXPIRE_AFTER_WRITE_DURATION, TimeUnit.MINUTES)
            .build();

    private static final String DEFAULT = "default";

    @Inject
    private AuthUserDao authUserDao;

    public void setAuthUserDao(AuthUserDao authUserDao) {
        this.authUserDao = authUserDao;
    }

    public AuthUser getUser(String service) {
        return Optional
                .ofNullable(soapAuthCache.getIfPresent(service))
                .orElseGet(() -> soapAuthCache.getIfPresent(DEFAULT));
    }

    @Override
    public void updateCache() {
        long startTime = System.currentTimeMillis();

        List<AuthUser> authUserList = authUserDao.findAllUsers();
        for (AuthUser authUser : authUserList) {
            soapAuthCache.put(authUser.getService(), authUser);
        }

        long endTime = System.currentTimeMillis();
        log.info(String.format(
                "%s were fully updated in '%s' milliseconds, the amount of newly put entries = '%s'",
                this.getClass().getName(),
                (endTime - startTime),
                authUserList.size()
        ));
    }

}
