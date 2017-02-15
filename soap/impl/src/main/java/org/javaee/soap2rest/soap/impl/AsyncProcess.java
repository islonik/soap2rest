package org.javaee.soap2rest.soap.impl;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;

/**
 * Created by nikilipa on 8/13/16.
 */
public class AsyncProcess implements Runnable {

    private final CamelManager camelManager;
    private final DSRequest dsRequest;

    public AsyncProcess(CamelManager camelManager, DSRequest dsRequest) {
        this.camelManager = camelManager;
        this.dsRequest = dsRequest;
    }

    @Override
    public void run() {
        camelManager.asyncProcess(dsRequest);
    }
}
