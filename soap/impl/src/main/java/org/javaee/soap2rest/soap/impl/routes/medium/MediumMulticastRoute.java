package org.javaee.soap2rest.soap.impl.routes.medium;

import org.apache.camel.builder.RouteBuilder;
import org.javaee.soap2rest.soap.impl.WildFlyResources;
import org.javaee.soap2rest.soap.impl.camel.S2RAggregationStrategy;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.logic.MulticastLogic;
import org.javaee.soap2rest.soap.impl.model.Service;
import org.javaee.soap2rest.soap.impl.model.ServiceType;
import org.javaee.soap2rest.soap.impl.routes.ExceptionRoute;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.javaee.soap2rest.soap.impl.services.RestServices;
import org.javaee.soap2rest.soap.impl.services.RouteServices;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * Created by nikilipa on 2/15/17.
 */
public class MediumMulticastRoute extends RouteBuilder {

    private final Logger log = LoggerFactory.getLogger(MediumMulticastRoute.class);

    private static final String MULTICAST_ROUTE_NAME = RouteServices.valueOf(ServiceType.MEDIUM, RouteServices.MULTICAST);

    private static final String MULTICAST_SWITCH = "direct:MediumMulticastSwitch";
    private static final String MULTICAST_SYNC = "direct:MediumMulticastSync";

    private static final String MULTICAST_GET = "direct:MediumMulticastGet";
    private static final String MULTICAST_PUT = "direct:MediumMulticastPut";

    private static final String JSON_GET = "JSON_GET";
    private static final String JSON_PUT = "JSON_PUT";

    private static final String START_WORK = "START_WORK";

    private static final Set<String> keys = ((Supplier<Set<String>>) () -> {
        Set<String> keys = new HashSet<>();
        keys.add(JSON_GET);
        keys.add(JSON_PUT);
        return keys;
    }).get();

    @Inject
    private MulticastLogic multicastLogic;

    @Inject
    private JsonServices jsonServices;

    @Inject
    private RestServices restServices;

    @Resource(name = WildFlyResources.SOAP_EXECUTOR)
    private ExecutorService executor;

    public void setMulticastLogic(MulticastLogic multicastLogic) {
        this.multicastLogic = multicastLogic;
    }

    public void setJsonServices(JsonServices jsonServices) {
        this.jsonServices = jsonServices;
    }

    public void setRestServices(RestServices restServices) {
        this.restServices = restServices;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .to(ExceptionRoute.ROUTE_EXCEPTION);

        from(MULTICAST_ROUTE_NAME)
                .routeId(MULTICAST_ROUTE_NAME)
                .process(exchange -> {
                    Service service = exchange.getIn().getBody(Service.class);
                    service.putOptional(START_WORK, Long.toString(System.currentTimeMillis()));
                    exchange.getIn().setBody(service);
                })
                .to(MULTICAST_SWITCH);

        from(MULTICAST_SWITCH)
                .routeId(MULTICAST_SWITCH)
                .multicast(new S2RAggregationStrategy(keys))
                .executorService(executor)
                .stopOnException()
                .parallelProcessing()
                .to(
                        MULTICAST_GET,
                        MULTICAST_PUT
                ).end()
                .to(MULTICAST_SYNC);

        from(MULTICAST_GET).routeId(MULTICAST_GET).process(exchange -> {
            exchange.getIn()
                    .setHeader(
                            JSON_GET,
                            multicastLogic.executeGet(exchange.getIn().getBody(Service.class))
                    );
        });

        from(MULTICAST_PUT).routeId(MULTICAST_PUT).process(exchange -> {
            exchange.getIn()
                    .setHeader(
                            JSON_PUT,
                            multicastLogic.executePut(exchange.getIn().getBody(Service.class))
                    );
        });

        from(MULTICAST_SYNC).routeId(MULTICAST_SYNC).process(exchange -> {
            Service service = exchange.getIn().getBody(Service.class);

            ServiceOrderStatus sosGet = exchange.getIn().getHeader(JSON_GET, ServiceOrderStatus.class);
            ServiceOrderStatus sosPut = exchange.getIn().getHeader(JSON_PUT, ServiceOrderStatus.class);

            long startTime = Long.parseLong(service.getOptional(START_WORK));
            long endTime = System.currentTimeMillis();
            String performanceMeasure = String.format(
                    "Performance medium measure: 2 sync (get, put) requests were executed in parallel for %s milliseconds",
                    (endTime - startTime)
            );
            log.info(performanceMeasure);

            ServiceOrderStatus outSos = multicastLogic.chooseBetweenEntities(service, performanceMeasure, sosGet, sosPut);
            exchange.getOut().setBody(outSos);
        });
    }

}
