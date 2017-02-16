package org.javaee.soap2rest.soap.web;

import org.javaee.soap2rest.soap.impl.CamelManager;
import org.javaee.soap2rest.soap.impl.WildFlyResources;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.HandleRequestPortType;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

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
        endpointInterface = "org.javaee.soap2rest.soap.impl.generated.ds.ws.HandleRequestPortType"
)
public class SoapWebService implements HandleRequestPortType {

    private final Logger log = LoggerFactory.getLogger(SoapWebService.class);

    @Resource
    private WebServiceContext wsContext;

    @Inject
    private ParserServices parserServices;

    @Inject
    private CamelManager camelManager;

    @Inject
    private WildFlyResources wildFlyResources;

    // http://localhost:8078/soap2rest/soap/v1/DeliverServiceWS
    @Override
    @RolesAllowed(SoapRoles.SOAP_ROLE)
    public DSResponse handleRequest(
            @WebParam(name = "DSRequest", targetNamespace = "http://www.nikilipa.org/SoapServiceRequest/v01", partName = "parameter")
                    final DSRequest dsRequest) {

        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest httpRequest = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);

        log.info(String.format(
                "%nWe accepted SOAP request! Request url = %s, Client Address = %s, Client Host = %s, Client Port = %s, User = %s",
                httpRequest.getRequestURL().toString(),
                httpRequest.getRemoteAddr(),
                httpRequest.getRemoteHost(),
                httpRequest.getRemotePort(),
                httpRequest.getRemoteUser()
        ));

        parserServices.setUpDsRequest(dsRequest);

        if (parserServices.isAsync(dsRequest)) {
            wildFlyResources.getExecutor().execute(() -> {
                camelManager.asyncProcess(dsRequest);
            });
            return parserServices.getAckDSResponse(dsRequest);
        }

        return camelManager.syncProcess(dsRequest);
    }
}
