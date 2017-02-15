package org.javaee.soap2rest.soap.impl.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by nikilipa on 2/15/17.
 */
@ApplicationScoped
public class MulticastLogic extends AbstractLogic {

    @Override
    public ServiceOrderStatus invoke(Service service) {
        throw new IllegalArgumentException("Don't invoke this method from Multicast!");
    }

    public ServiceOrderStatus executeGet(Service service) {
        ServiceOrderStatus sos = restServices.sendGetRequest(
                service,
                "%s/sync/response"
        );
        return sos;
    }

    public ServiceOrderStatus executePut(Service service) throws JsonProcessingException {
        ServiceOrderStatus sos = restServices.sendPutRequest(
                service,
                "%s/sync/response",
                jsonServices.objectToJson(service.getParams())
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

}
