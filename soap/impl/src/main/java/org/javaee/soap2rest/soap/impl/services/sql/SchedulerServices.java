package org.javaee.soap2rest.soap.impl.services.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;

/**
 * Created by nikilipa on 2/23/17.
 */
@Startup
@Singleton
@Lock(LockType.READ)
public class SchedulerServices {

    private static final Logger log = LoggerFactory.getLogger(SchedulerServices.class);

    @Inject
    private DbAuthServices dbAuthServices;

    @PostConstruct
    public void setUp() {
        callAuthService();
    }

    @Schedule(second = "10", minute = "*/10", hour = "*", persistent = false)
    public void callAuthService() {
        log.debug("Call SchedulerService.callAuthService()");

        dbAuthServices.updateCache();
    }

}
