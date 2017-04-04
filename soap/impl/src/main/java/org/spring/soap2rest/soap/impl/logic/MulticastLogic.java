package org.spring.soap2rest.soap.impl.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.generated.ds.ws.StatusType;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.ParserServices;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikilipa on 2/15/17.
 */
@Component
public class MulticastLogic extends AbstractLogic {

    private static final Logger log = LoggerFactory.getLogger(MulticastLogic.class);

    public static final Long MULTICAST_TIMEOUT = 5000L;
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

    public ServiceOrderStatus endlessCycle(Service service) {
        try {
            while (true) {
                log.warn("In the endless cycle.");
                Thread.sleep(500L);
            }
        } catch (InterruptedException e) {
            log.warn("The endless cycle was interrupted.");
        }
        return null;
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

    public static ServiceOrderStatus chooseBetweenEntities(List<Future<ServiceOrderStatus>> sosList) {
        int code = 0;
        ServiceOrderStatus survivor = new ServiceOrderStatus();
        StatusType statusType = new StatusType();
        survivor.setStatusType(statusType);

        List<Future<ServiceOrderStatus>> exceptionList = new ArrayList<>();
        try {
            if (sosList != null) {
                for (Future<ServiceOrderStatus> futureSos : sosList) {
                    exceptionList.add(futureSos);
                }
                for (Future<ServiceOrderStatus> futureSos : sosList) {
                    ServiceOrderStatus sos = futureSos.get(MULTICAST_TIMEOUT, TimeUnit.MILLISECONDS);
                    if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
                        int tempCode = Integer.parseInt(sos.getStatusType().getCode());
                        if (code < tempCode) {
                            code = tempCode;
                            survivor = sos;
                        }
                    }
                }
                if (survivor.getStatusType().getCode() != null) {
                    return survivor;
                }
                if (!sosList.isEmpty()) {
                    return sosList.get(0).get(MULTICAST_TIMEOUT, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Exception e) {
            for (Future<ServiceOrderStatus> futureSos : exceptionList) {
                futureSos.cancel(true);
            }

            statusType.setCode("504");
            statusType.setDesc("Multicast timeout");
            survivor.setStatusType(statusType);
            return survivor;
        }
        statusType.setCode("500");
        statusType.setDesc("Result list is null or empty");
        survivor.setStatusType(statusType);
        return survivor;
    }

}
