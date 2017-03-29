package org.spring.soap2rest.soap.impl.routes.slow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.logic.MulticastLogic;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.RouteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

/**
 * Created by nikilipa on 3/29/17.
 */
@Component
public class SlowMulticastRoute {

    private static final Logger log = LoggerFactory.getLogger(SlowMulticastRoute.class);

    @Autowired
    private MulticastLogic multicastLogic;

    @ServiceActivator(inputChannel = RouteServices.SLOW_MULTICAST_ID)
    public ServiceOrderStatus processOrder(Service service) {
        log.info("SlowMulticastRoute");
        return multicastLogic.slow(service);
    }

}
