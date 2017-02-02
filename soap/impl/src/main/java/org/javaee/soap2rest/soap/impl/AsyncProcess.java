package org.javaee.soap2rest.soap.impl;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.soap.impl.services.SoapOrchestrator;

/**
 * Created by nikilipa on 8/13/16.
 */
public class AsyncProcess implements Runnable {

    private final SoapOrchestrator soapOrchestrator;
    private final DSRequest dsRequest;

    public AsyncProcess(SoapOrchestrator soapOrchestrator, DSRequest dsRequest) {
        this.soapOrchestrator = soapOrchestrator;
        this.dsRequest = dsRequest;
    }

    @Override
    public void run() {
        soapOrchestrator.asyncProcess(dsRequest);
    }
}
