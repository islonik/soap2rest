package org.spring.soap2rest.soap.impl.services;

import org.spring.soap2rest.soap.impl.model.ServiceType;

/**
 * Created by nikilipa on 2/15/17.
 */
public class RouteServices {

    public static final String ASYNC = "Async";
    public static final String MULTICAST = "Multicast";
    public static final String SYNC = "Sync";

    public static final String FAST_ASYNC_ID = ServiceType.FAST + RouteServices.ASYNC;
    public static final String FAST_MULTICAST_ID = ServiceType.FAST + RouteServices.MULTICAST;
    public static final String FAST_SYNC_ID = ServiceType.FAST + RouteServices.SYNC;

    public static final String MEDIUM_ASYNC_ID = ServiceType.MEDIUM + RouteServices.ASYNC;
    public static final String MEDIUM_MULTICAST_ID = ServiceType.MEDIUM + RouteServices.MULTICAST;
    public static final String MEDIUM_SYNC_ID = ServiceType.MEDIUM + RouteServices.SYNC;

    public static final String SLOW_ASYNC_ID = ServiceType.SLOW + RouteServices.ASYNC;
    public static final String SLOW_MULTICAST_ID = ServiceType.SLOW + RouteServices.MULTICAST;
    public static final String SLOW_SYNC_ID = ServiceType.SLOW + RouteServices.SYNC;

    public static String valueOf(String name) {
        if (name.equalsIgnoreCase(ASYNC)) {
            return ASYNC;
        } else if (name.equalsIgnoreCase(MULTICAST)) {
            return MULTICAST;
        } else if (name.equalsIgnoreCase(SYNC)) {
            return SYNC;
        } else {
            throw new IllegalArgumentException(String.format("Unknown '%s' service name.", name));
        }
    }

    public static String valueOf(String serviceType, String name) {
        return serviceType + name;
    }

}
