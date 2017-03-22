package org.spring.soap2rest.soap.impl.services.db;

/**
 * Created by nikilipa on 3/22/17.
 */
public interface SoapScheduler {

    Long EXPIRE_AFTER_WRITE_DURATION = 20L; // minutes

    void updateCache();

}