package org.spring.soap2rest.soap.impl.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.generated.ds.ws.StatusType;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.ParserServices;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by nikilipa on 2/15/17.
 */
@Component
public class MulticastLogic extends AbstractLogic {

    public static final String AGGREGATE_CHANNEL = "aggregatorChannel";

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

    public ServiceOrderStatus executeSyncGet(Service service) {
        ServiceOrderStatus sos = restServices.sendGetRequest(
                service,
                "%s/sync/response"
        );
        return sos;
    }

    public ServiceOrderStatus executeAsyncGet(Service service) {
        ServiceOrderStatus sos = restServices.sendGetRequest(
                service,
                "%s/async/response"
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

    public ServiceOrderStatus executeTimeout(Service service) {
        ServiceOrderStatus sos = restServices.sendGetRequest(
                service,
                "%s/async/timeout"
        );
        return sos;
    }

    public static ServiceOrderStatus chooseBetweenEntities(List<ServiceOrderStatus> sosList) {
        int code = 0;
        ServiceOrderStatus survivor = null;
        if (sosList != null) {
            for (ServiceOrderStatus sos : sosList) {
                if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
                    int tempCode = Integer.parseInt(sos.getStatusType().getCode());
                    if (code < tempCode) {
                        code = tempCode;
                        survivor = sos;
                    }
                }
            }
            if (survivor != null) {
                return survivor;
            }
            if (!sosList.isEmpty()) {
                return sosList.get(0);
            }
        }
        survivor = new ServiceOrderStatus();
        StatusType statusType = new StatusType();
        statusType.setCode("500");
        statusType.setDesc("Result list is null or empty");
        survivor.setStatusType(statusType);
        return survivor;
    }

}
