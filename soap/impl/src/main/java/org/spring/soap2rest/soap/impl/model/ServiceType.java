package org.spring.soap2rest.soap.impl.model;

import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrder;

/**
 * Created by nikilipa on 8/31/16.
 */
public class ServiceType {

    public static final String FAST = "Fast";
    public static final String MEDIUM = "Medium";
    public static final String SLOW = "Slow";

    private static final String UNKNOWN = "Unknown";

    public static String valueOf(ServiceOrder serviceOrder) {
        String action = UNKNOWN;
        if (serviceOrder != null && serviceOrder.getServiceType() != null) {
            action = serviceOrder.getServiceType();
        }
        if (FAST.equalsIgnoreCase(action)) {
            return FAST;
        } else if (MEDIUM.equalsIgnoreCase(action)) {
            return MEDIUM;
        } else if (SLOW.equalsIgnoreCase(action)) {
            return SLOW;
        } else {
            throw new IllegalArgumentException(String.format("Unknown '%s' service type.", action));
        }
    }
}
