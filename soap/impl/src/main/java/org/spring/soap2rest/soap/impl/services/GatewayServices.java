package org.spring.soap2rest.soap.impl.services;

import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.model.Service;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Created by nikilipa on 3/29/17.
 */
@MessagingGateway
public interface GatewayServices {

    String DEFAULT_CHANNEL = "pipeline";

    @Gateway(requestChannel = DEFAULT_CHANNEL)
    ServiceOrderStatus route(Service service);

}
