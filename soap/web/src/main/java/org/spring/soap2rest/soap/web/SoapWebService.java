package org.spring.soap2rest.soap.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.soap.impl.AsyncProcess;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.spring.soap2rest.soap.impl.generated.ds.ws.HandleRequestPortType;
import org.spring.soap2rest.soap.impl.services.ParserServices;
import org.spring.soap2rest.soap.impl.services.SoapOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.jws.WebParam;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nikilipa on 2/2/17.
 */

@Endpoint
public class SoapWebService implements HandleRequestPortType {

    private static final Logger log = LoggerFactory.getLogger(SoapWebService.class);

    @Autowired
    private ParserServices parserServices;

    @Autowired
    private SoapOrchestrator soapOrchestrator;

    private ExecutorService executor = Executors.newCachedThreadPool();

    // http://localhost:8077/soap2rest/v1/soap/DeliverServiceWS
    @Override
    @PayloadRoot(localPart = "DSRequest", namespace = "http://www.nikilipa.org/SoapServiceRequest/v01")
    @ResponsePayload
    public DSResponse handleRequest(
            //@WebParam(name = "DSRequest", targetNamespace = "http://www.nikilipa.org/SoapServiceRequest/v01", partName = "parameter")
            @RequestPayload DSRequest dsRequest) {

        //MessageContext mc = wsContext.getMessageContext();
        //HttpServletRequest httpRequest = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);

        /*log.info(String.format(
                "%nWe accepted SOAP request! Request url = %s, Client Address = %s, Client Host = %s, Client Port = %s, User = %s",
                httpRequest.getRequestURL().toString(),
                httpRequest.getRemoteAddr(),
                httpRequest.getRemoteHost(),
                httpRequest.getRemotePort(),
                httpRequest.getRemoteUser()
        ));*/

        log.info("We got a request");

        parserServices.setUpDsRequest(dsRequest);

        if (parserServices.isAsync(dsRequest)) {
            executor.execute(new AsyncProcess(soapOrchestrator, dsRequest));
            return parserServices.getAckDSResponse(dsRequest);
        }

        return soapOrchestrator.syncProcess(dsRequest);
    }


}
