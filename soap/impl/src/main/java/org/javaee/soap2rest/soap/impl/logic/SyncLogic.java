package org.javaee.soap2rest.soap.impl.logic;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Created by nikilipa on 2/15/17.
 */
@ApplicationScoped
public class SyncLogic extends AbstractLogic {

    private final Logger log = LoggerFactory.getLogger(SyncLogic.class);

    @Override
    public ServiceOrderStatus invoke(Service service) {
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

        log.info(String.format(
                "Performance measure: 3 sync get requests were executed in %s milliseconds",
                (endTime - startTime)
        ));

        return sos;
    }

}
