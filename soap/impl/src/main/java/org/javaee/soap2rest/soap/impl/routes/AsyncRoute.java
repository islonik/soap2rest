package org.javaee.soap2rest.soap.impl.routes;

import org.apache.camel.builder.RouteBuilder;
import org.javaee.soap2rest.soap.impl.logic.AsyncLogic;
import org.javaee.soap2rest.soap.impl.logic.CamelInvoker;
import org.javaee.soap2rest.soap.impl.services.RouteServices;

import javax.inject.Inject;

/**
 * Created by nikilipa on 2/15/17.
 */
public class AsyncRoute extends RouteBuilder {

    private static final String ASYNC_ROUTE_NAME = RouteServices.valueOf(RouteServices.ASYNC);

    @Inject
    private AsyncLogic asyncLogic;

    public void setAsyncLogic(AsyncLogic asyncLogic) {
        this.asyncLogic = asyncLogic;
    }

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .to(ExceptionRoute.ROUTE_EXCEPTION);

        from(ASYNC_ROUTE_NAME)
                .routeId(ASYNC_ROUTE_NAME)
                .bean(asyncLogic, CamelInvoker.METHOD);
    }

}
