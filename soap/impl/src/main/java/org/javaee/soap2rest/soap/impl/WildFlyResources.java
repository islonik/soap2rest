package org.javaee.soap2rest.soap.impl;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;

/**
 * Created by nikilipa on 2/1/17.
 */
@ApplicationScoped
public class WildFlyResources {

    public static final String SOAP_EXECUTOR = "java:/s2r/threads/executors/soap";

    @Resource(name = WildFlyResources.SOAP_EXECUTOR)
    private ExecutorService executor;

    public ExecutorService getExecutor() {
        return executor;
    }
}
