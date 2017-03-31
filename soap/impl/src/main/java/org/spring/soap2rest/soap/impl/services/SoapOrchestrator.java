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
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 8/13/16.
 */
@Component
public class SoapOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SoapOrchestrator.class);

    @Autowired
    private JsonServices jsonServices;

    @Autowired
    private ParserServices parserServices;

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

            ServiceOrderStatus sos = gatewayServices.route(service);

            if (sos == null) { // defaultReplyTimeout means null from route method
                throw new TimeoutException("Gateway timeout");
            }
            return parserServices.getDSResponse(dsRequest, sos);
        } catch (IllegalArgumentException | NoSuchBeanDefinitionException | TimeoutException e) {
            log.warn(e.getMessage());
            return parserServices.getDSResponse(dsRequest, "500", e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return parserServices.getDSResponse(dsRequest, "500", e.getMessage());
        }
    }
}
