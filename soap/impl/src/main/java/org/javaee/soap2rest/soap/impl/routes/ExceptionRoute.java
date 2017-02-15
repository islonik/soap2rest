package org.javaee.soap2rest.soap.impl.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.StatusType;
import org.javaee.soap2rest.soap.impl.model.Service;
import org.javaee.soap2rest.soap.impl.services.ParserServices;

/**
 * Created by nikilipa on 2/15/17.
 */
public class ExceptionRoute extends RouteBuilder {

    public static final String ROUTE_EXCEPTION = "direct:RouteException";

    public void configure() {
        from(ROUTE_EXCEPTION).routeId(ROUTE_EXCEPTION).process(exchange -> {
            Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

            ServiceOrderStatus response = new ServiceOrderStatus();
            StatusType statusType = new StatusType();
            statusType.setCode(ParserServices.CODE_BUG);
            statusType.setDesc(e.getMessage());
            response.setStatusType(statusType);

            Object object = exchange.getIn().getBody();
            Service service = null;
            if (object instanceof Service) {
                service = exchange.getIn().getBody(Service.class);

                log.warn(String.format("Service %s was interrupted by %s", service.toString(), e.getMessage()));
            }

            exchange.getIn().setBody(response);
        });
    }

}
