package org.javaee.soap2rest.soap.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.javaee.soap2rest.soap.impl.camel.CamelStarter;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.javaee.soap2rest.soap.impl.services.RouteServices;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 2/15/17.
 */
@Singleton
public class CamelManager {

    private final Logger log = LoggerFactory.getLogger(CamelManager.class);

    public static final Long SYNC_TIMEOUT = 12L; // sec
    public static final Long ASYNC_TIMEOUT = 5L; // min

    private final JsonServices jsonServices;
    private final ParserServices parserServices;

    private final CamelContext camelContext;
    private final ProducerTemplate template;

    @Inject
    public CamelManager(JsonServices jsonServices, ParserServices parserServices, CamelStarter camelStarter) {
        this.jsonServices = jsonServices;
        this.parserServices = parserServices;
        this.camelContext = camelStarter.getCamelContext();
        this.template = camelStarter.getTemplate();
    }

    public DSResponse syncProcess(DSRequest dsRequest) {
        return process(dsRequest, SYNC_TIMEOUT, TimeUnit.SECONDS);
    }

    public void asyncProcess(DSRequest dsRequest) {
        try {
            DSResponse dsResponse = process(dsRequest, ASYNC_TIMEOUT, TimeUnit.MINUTES);
            log.info("\n" + jsonServices.objectToJson(dsResponse));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private DSResponse process(DSRequest dsRequest, Long timeout, TimeUnit unit) {
        try {
            Service service = parserServices.xml2service(dsRequest);
            String endpoint = RouteServices.valueOf(service.getType(), service.getName());

            if (camelContext.hasEndpoint(endpoint) == null) {
                DSResponse dsResponse = parserServices.getFailDSResponse(
                        dsRequest,
                        String.format("Service '%s' is not implemented yet!", service.getName())
                );
                return dsResponse;
            }

            ServiceOrderStatus sos = (ServiceOrderStatus) template
                    .asyncRequestBody(endpoint, service)
                    .get(timeout, unit);

            return parserServices.getDSResponse(dsRequest, sos);
        } catch (TimeoutException te) {
            return parserServices.getTimeoutDSResponse(dsRequest, te.getMessage());
        } catch (Exception e) {
            return parserServices.getFailDSResponse(dsRequest, e.getMessage());
        }
    }
}
