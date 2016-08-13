package org.javaee.soap2rest.web.soap;

import org.javaee.soap2rest.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.impl.generated.ds.ws.HandleRequestPortType;
import org.javaee.soap2rest.impl.soap.AsyncRequest;
import org.javaee.soap2rest.impl.soap.services.ParserService;
import org.javaee.soap2rest.impl.soap.services.SoapOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nikilipa on 8/12/16.
 */
@RolesAllowed({
        SoapRoles.SOAP_ROLE
})
@WebService(
        serviceName = "DeliverServiceWS",
        targetNamespace = "http://nikilipa.org/Service/DeliverService/v01/DeliverServiceWS",
        portName = "DeliverServiceWS_Port",
        name = "DeliverServiceWS",
        wsdlLocation = "classpath:/wsdl/ds.wsdl",
        endpointInterface = "org.javaee.soap2rest.impl.generated.ds.ws.HandleRequestPortType"
)
public class SoapWebService implements HandleRequestPortType {

    private final Logger log = LoggerFactory.getLogger(SoapWebService.class);

    @Inject
    private ParserService parserService;

    @Inject
    private SoapOrchestrator soapOrchestrator;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    // http://localhost:8080/soap2rest/soap/v1/DeliverServiceWS
    @Override
    @RolesAllowed(SoapRoles.SOAP_ROLE)
    public DSResponse handleRequest(
            @WebParam(name = "DSRequest", targetNamespace = "http://www.nikilipa.org/SoapServiceRequest/v01", partName = "parameter")
                    DSRequest dsRequest) {
        log.info("We accepted SOAP request!");

        parserService.setUpDsRequest(dsRequest);

        if (parserService.isAsync(dsRequest)) {
            executorService.execute(new AsyncRequest(soapOrchestrator, dsRequest));
            return parserService.getAckDSResponse(dsRequest);
        }

        return soapOrchestrator.syncProcess(dsRequest);
    }
}
