package org.javaee.soap2rest.rest.api.model;

import java.util.concurrent.Future;

/**
 * Created by nikilipa on 3/24/17.
 */
public class AsyncInterrupter {

    private volatile Future task;

    public void setFuture(Future task) {
        this.task = task;
    }

    public Future getTask() {
        return task;
    }

}
