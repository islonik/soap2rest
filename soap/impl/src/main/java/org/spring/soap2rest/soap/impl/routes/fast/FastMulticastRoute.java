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

    public static final String SUBSCRIBE_FAST = "subscribeFast";
    public static final String FAST_CHAHHEL_1 = "fastChannel1";
    public static final String FAST_CHAHHEL_2 = "fastChannel2";
    public static final String FAST_CHAHHEL_3 = "fastChannel3";

    @Autowired
    private MulticastLogic multicastLogic;

    // outputChannel has a method name in ImplConfiguration
    @ServiceActivator(inputChannel = RouteServices.FAST_MULTICAST_ID, outputChannel = SUBSCRIBE_FAST)
    public Service processOrder(Service service) {
        log.info("FastMulticastRoute");
        return service;
    }

    @ServiceActivator(inputChannel = FAST_CHAHHEL_1, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public ServiceOrderStatus channel1(Service service) {
        log.info("fast1");
        return multicastLogic.executeSyncGet(service);
    }

    @ServiceActivator(inputChannel = FAST_CHAHHEL_2, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public ServiceOrderStatus channel2(Service service) {
        log.info("fast2");
        return multicastLogic.executeSyncGet(service);
    }

    @ServiceActivator(inputChannel = FAST_CHAHHEL_3, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public ServiceOrderStatus channel3(Service service) {
        log.info("fast3");
        return multicastLogic.executeSyncGet(service);
    }

}
