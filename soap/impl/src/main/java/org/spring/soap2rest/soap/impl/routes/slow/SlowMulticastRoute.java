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

    @ServiceActivator(inputChannel = RouteServices.SLOW_MULTICAST_ID, outputChannel = "subscribeSlow")
    public Service processOrder(Service service) {
        log.info("SlowMulticastRoute");
        return service;
    }

    @ServiceActivator(inputChannel = "slowChannel1", outputChannel = "aggregatorChannel")
    public ServiceOrderStatus get1(Service service) {
        log.info("slow1");
        return multicastLogic.executeGet(service);
    }

    @ServiceActivator(inputChannel = "slowChannel2", outputChannel = "aggregatorChannel")
    public ServiceOrderStatus get2(Service service) {
        log.info("slow2");
        return multicastLogic.executeGet(service);
    }

    @ServiceActivator(inputChannel = "slowChannel3", outputChannel = "aggregatorChannel")
    public ServiceOrderStatus get3(Service service) {
        log.info("slow3");
        return multicastLogic.executeGet(service);
    }

}
