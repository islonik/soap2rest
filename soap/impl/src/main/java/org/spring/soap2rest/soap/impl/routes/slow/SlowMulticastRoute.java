package org.spring.soap2rest.soap.impl.routes.slow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.logic.MulticastLogic;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.RouteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Created by nikilipa on 3/29/17.
 */
@Component
public class SlowMulticastRoute {

    private static final Logger log = LoggerFactory.getLogger(SlowMulticastRoute.class);

    public static final String SUBSCRIBE_SLOW = "subscribeSlow";
    public static final String SLOW_CHAHHEL_1 = "slowChannel1";
    public static final String SLOW_CHAHHEL_2 = "slowChannel2";
    public static final String SLOW_CHAHHEL_3 = "slowChannel3";

    @Autowired
    private MulticastLogic multicastLogic;

    @ServiceActivator(inputChannel = RouteServices.SLOW_MULTICAST_ID, outputChannel = SUBSCRIBE_SLOW)
    public Service processOrder(Service service) {
        log.info("SlowMulticastRoute");
        return service;
    }

    @Async
    @ServiceActivator(inputChannel = SLOW_CHAHHEL_1, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public Future<ServiceOrderStatus> channel1(Service service) {
        log.info("slow1");
        return new AsyncResult(multicastLogic.executeTimeout(service));
    }

    @Async
    @ServiceActivator(inputChannel = SLOW_CHAHHEL_2, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public Future<ServiceOrderStatus> channel2(Service service) {
        log.info("slow2");
        return new AsyncResult(multicastLogic.endlessCycle(service));
    }

    @Async
    @ServiceActivator(inputChannel = SLOW_CHAHHEL_3, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public Future<ServiceOrderStatus> channel3(Service service) {
        log.info("slow3");
        return new AsyncResult(multicastLogic.endlessCycle(service));
    }

}
