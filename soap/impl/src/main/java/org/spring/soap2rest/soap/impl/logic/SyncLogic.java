package org.spring.soap2rest.soap.impl.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.ParserServices;
import org.springframework.stereotype.Component;

/**
 * Created by nikilipa on 2/15/17.
 */
@Component
public class SyncLogic extends AbstractLogic {

    private final Logger log = LoggerFactory.getLogger(SyncLogic.class);

    @Override
    public ServiceOrderStatus fast(Service service) {
        long startTime = System.currentTimeMillis();

        // 1 request
        ServiceOrderStatus sos = restServices.sendGetRequest(service, "%s/sync/response");
        if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
            return sos;
        }

        long endTime = System.currentTimeMillis();

        String result = String.format(
                "Performance fast measure: 1 sync get requests was executed in %s milliseconds",
                (endTime - startTime)
        );
        log.info(result);
        setUpPerformanceMetrics(service, sos, result);

        return sos;
    }

    @Override
    public ServiceOrderStatus medium(Service service) {
        long startTime = System.currentTimeMillis();

        // 1 request
        ServiceOrderStatus sos = restServices.sendGetRequest(service, "%s/sync/response");
        if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
            return sos;
        }
        // 2 request
        sos = restServices.sendGetRequest(service, "%s/sync/response");
        if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
            return sos;
        }

        long endTime = System.currentTimeMillis();

        String result = String.format(
                "Performance medium measure: 2 sync get requests were executed in %s milliseconds",
                (endTime - startTime)
        );
        log.info(result);
        setUpPerformanceMetrics(service, sos, result);

        return sos;
    }

    @Override
    public ServiceOrderStatus slow(Service service) {
        long startTime = System.currentTimeMillis();

        // 1 request
        ServiceOrderStatus sos = restServices.sendGetRequest(service, "%s/sync/response");
        if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
            return sos;
        }
        // 2 request
        sos = restServices.sendGetRequest(service, "%s/sync/response");
        if (!sos.getStatusType().getCode().equals(ParserServices.CODE_OK)) {
            return sos;
        }
        // 3 request
        sos = restServices.sendGetRequest(service, "%s/sync/response");
        long endTime = System.currentTimeMillis();

        String result = String.format(
                "Performance slow measure: 3 sync get requests were executed in %s milliseconds",
                (endTime - startTime)
        );
        log.info(result);
        setUpPerformanceMetrics(service, sos, result);


        return sos;
    }

}