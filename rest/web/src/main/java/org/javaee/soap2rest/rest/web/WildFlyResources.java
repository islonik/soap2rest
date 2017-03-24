package org.javaee.soap2rest.rest.web;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;

/**
 * Created by nikilipa on 2/1/17.
 */
@ApplicationScoped
public class WildFlyResources {

    public static final String REST_EXECUTOR = "java:/s2r/threads/executors/rest";

    @Resource(name = WildFlyResources.REST_EXECUTOR)
    private ExecutorService executor;

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
