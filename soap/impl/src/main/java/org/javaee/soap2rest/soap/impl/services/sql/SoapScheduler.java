package org.javaee.soap2rest.soap.impl.services.sql;

/**
 * Created by nikilipa on 2/27/17.
 */
public interface SoapScheduler {

    Long EXPIRE_AFTER_WRITE_DURATION = 20L; // minutes

    void updateCache();

}
