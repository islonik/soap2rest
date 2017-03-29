package org.spring.soap2rest.soap.impl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.utils.services.JsonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nikilipa on 8/13/16.
 */
@Component
public class SoapOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SoapOrchestrator.class);

    public static final Long SYNC_TIMEOUT = 12L; // sec
    public static final Long ASYNC_TIMEOUT = 5L; // min

    @Autowired
    private JsonServices jsonServices;

    @Autowired
    private ParserServices parserServices;

    @Autowired
    private RestServices restServices;

    @Autowired
    private GatewayServices gatewayServices;

    public DSResponse syncProcess(DSRequest dsRequest) {
        return process(dsRequest);
    }

    public void asyncProcess(DSRequest dsRequest) {
        try {
            DSResponse dsResponse = process(dsRequest);
            log.info("\n" + jsonServices.objectToJson(dsResponse));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private DSResponse process(DSRequest dsRequest) {
        try {
            Service service = parserServices.xml2service(dsRequest);
            //String endpoint = RouteServices.valueOf(service.getType(), service.getName());

            ServiceOrderStatus sos = gatewayServices.route(service);
            /*if (service.getName().equalsIgnoreCase(RouteServices.SYNC)) {
                sos = restServices.sendGetRequest(service, "%s/sync/response");
            } else if (service.getName().equalsIgnoreCase(RouteServices.ASYNC)) {
                sos = restServices.sendGetRequest(service, "%s/async/response");
            } else {
                sos = restServices.sendGetRequest(service, "%s/async/response");

                AsyncRestRequest asyncRestRequest = new AsyncRestRequest();
                asyncRestRequest.setMessageId(dsRequest.getHeader().getMessageId());
                asyncRestRequest.setConversationId(dsRequest.getHeader().getConversationId());
                asyncRestRequest.setCode(sos.getStatusType().getCode());
                asyncRestRequest.setDesc(sos.getStatusType().getDesc());

                sos = restServices.sendPutRequest(service, "%s/async/notify", asyncRestRequest);
            }*/
            return parserServices.getDSResponse(dsRequest, sos);
        } /*catch (TimeoutException te) {
            return parserServices.getDSResponse(dsRequest, "504", te.getMessage());
        } */catch (Exception e) {
            log.error(e.getMessage(), e);
            return parserServices.getDSResponse(dsRequest, "500", e.getMessage());
        }
    }
}
