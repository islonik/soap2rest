package org.spring.soap2rest.soap.impl.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.ParserServices;
import org.springframework.stereotype.Component;

/**
 * Created by nikilipa on 2/15/17.
 */
@Component
public class MulticastLogic extends AbstractLogic {

    @Override
    public ServiceOrderStatus fast(Service service) {
        throw new IllegalArgumentException("Don't invoke 'fast' method from Multicast!");
    }

    @Override
    public ServiceOrderStatus medium(Service service) {
        throw new IllegalArgumentException("Don't invoke 'medium' method from Multicast!");
    }

    @Override
    public ServiceOrderStatus slow(Service service) {
        throw new IllegalArgumentException("Don't invoke 'slow' method from Multicast!");
    }

    public ServiceOrderStatus executeGet(Service service) {
        ServiceOrderStatus sos = restServices.sendGetRequest(
                service,
                "%s/sync/response"
        );
        return sos;
    }

    public ServiceOrderStatus executePut(Service service, AsyncRestRequest asyncRestRequest) throws JsonProcessingException {
        ServiceOrderStatus sos = restServices.sendPutRequest(
                service,
                "%s/sync/response",
                asyncRestRequest
        );
        return sos;
    }

    public ServiceOrderStatus executePost(Service service) throws JsonProcessingException {
        ServiceOrderStatus sos = restServices.sendPostRequest(
                service,
                "%s/sync/response",
                jsonServices.objectToJson(service.getParams())
        );
        return sos;
    }

    public ServiceOrderStatus chooseBetweenEntities(Service service, String message, ServiceOrderStatus... sosList) {
        if (sosList != null) {
            for (ServiceOrderStatus sos : sosList) {
                if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
                    setUpPerformanceMetrics(service, sos, message);
                    return sos;
                }
            }
            ServiceOrderStatus sos = sosList[0];
            setUpPerformanceMetrics(service, sos, message);
            return sos;
        }
        return null;
    }

}
