package org.spring.soap2rest.soap.impl.routes;

import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.services.GatewayServices;
import org.spring.soap2rest.soap.impl.services.RouteServices;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;

/**
 * Created by nikilipa on 3/29/17.
 */
@MessageEndpoint
public class ServiceRouter {

    @Router(inputChannel= GatewayServices.DEFAULT_CHANNEL)
    public String process(Service service) { // return channel name
        return RouteServices.valueOf(service.getType(), service.getName());
    }

}
