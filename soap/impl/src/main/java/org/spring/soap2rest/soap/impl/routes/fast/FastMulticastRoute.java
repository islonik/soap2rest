package org.spring.soap2rest.soap.impl.routes.fast;

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
public class FastMulticastRoute {

    private static final Logger log = LoggerFactory.getLogger(FastMulticastRoute.class);

    @Autowired
    private MulticastLogic multicastLogic;

    // outputChannel has a method name in ImplConfiguration
    @ServiceActivator(inputChannel = RouteServices.FAST_MULTICAST_ID, outputChannel = "subscribeFast")
    public Service processOrder(Service service) {
        log.info("FastMulticastRoute");
        return service;
    }

    @ServiceActivator(inputChannel = "fastChannel1", outputChannel = "aggregatorChannel")
    public ServiceOrderStatus get1(Service service) {
        log.info("fast1");
        return multicastLogic.executeGet(service);
    }

    @ServiceActivator(inputChannel = "fastChannel2", outputChannel = "aggregatorChannel")
    public ServiceOrderStatus get2(Service service) {
        log.info("fast2");
        return multicastLogic.executeGet(service);
    }

    @ServiceActivator(inputChannel = "fastChannel3", outputChannel = "aggregatorChannel")
    public ServiceOrderStatus get3(Service service) {
        log.info("fast3");
        return multicastLogic.executeGet(service);
    }

}
