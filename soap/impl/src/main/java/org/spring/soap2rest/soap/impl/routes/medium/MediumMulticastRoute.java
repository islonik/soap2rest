package org.spring.soap2rest.soap.impl.routes.medium;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class MediumMulticastRoute {

    private static final Logger log = LoggerFactory.getLogger(MediumMulticastRoute.class);

    public static final String SUBSCRIBE_MEDIUM = "subscribeMedium";
    public static final String MEDIUM_CHAHHEL_1 = "mediumChannel1";
    public static final String MEDIUM_CHAHHEL_2 = "mediumChannel2";
    public static final String MEDIUM_CHAHHEL_3 = "mediumChannel3";

    @Autowired
    private MulticastLogic multicastLogic;

    @ServiceActivator(inputChannel = RouteServices.MEDIUM_MULTICAST_ID, outputChannel = SUBSCRIBE_MEDIUM)
    public Service processOrder(Service service) {
        log.info("MediumMulticastRoute");
        return service;
    }

    @Async
    @ServiceActivator(inputChannel = MEDIUM_CHAHHEL_1, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public Future<ServiceOrderStatus> channel1(Service service) throws JsonProcessingException {
        log.info("medium1");
        return new AsyncResult(multicastLogic.executePost(service));
    }

    @Async
    @ServiceActivator(inputChannel = MEDIUM_CHAHHEL_2, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public Future<ServiceOrderStatus> channel2(Service service) throws JsonProcessingException {
        log.info("medium2");
        return new AsyncResult(multicastLogic.executeAsyncGet(service));
    }

    @Async
    @ServiceActivator(inputChannel = MEDIUM_CHAHHEL_3, outputChannel = MulticastLogic.AGGREGATE_CHANNEL)
    public Future<ServiceOrderStatus> channel3(Service service) throws JsonProcessingException {
        log.info("medium3");
        return new AsyncResult(multicastLogic.endlessCycle(service));
    }

}
