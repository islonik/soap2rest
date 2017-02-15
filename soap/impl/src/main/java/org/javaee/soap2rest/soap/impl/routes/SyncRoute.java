package org.javaee.soap2rest.soap.impl.routes;

import org.apache.camel.builder.RouteBuilder;
import org.javaee.soap2rest.soap.impl.logic.CamelInvoker;
import org.javaee.soap2rest.soap.impl.logic.SyncLogic;
import org.javaee.soap2rest.soap.impl.services.RouteServices;

import javax.inject.Inject;

/**
 * Created by nikilipa on 2/15/17.
 */
public class SyncRoute extends RouteBuilder {

    private static final String SYNC_ROUTE_NAME = RouteServices.valueOf(RouteServices.SYNC);

    @Inject
    private SyncLogic syncLogic;

    public void setSyncLogic(SyncLogic syncLogic) {
        this.syncLogic = syncLogic;
    }

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .to(ExceptionRoute.ROUTE_EXCEPTION);

        from(SYNC_ROUTE_NAME)
                .routeId(SYNC_ROUTE_NAME)
                .bean(syncLogic, CamelInvoker.METHOD);
    }

}
