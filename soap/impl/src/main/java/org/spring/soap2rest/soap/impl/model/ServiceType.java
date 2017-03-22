package org.spring.soap2rest.soap.impl.model;

import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrder;

/**
 * Created by nikilipa on 8/31/16.
 */
public enum ServiceType {

    FAST,
    MEDIUM,
    SLOW;

    private static final String UNKNOWN = "Unknown";

    public static ServiceType valueOf(ServiceOrder serviceOrder) {
        String action = UNKNOWN;
        if (serviceOrder != null && serviceOrder.getServiceType() != null) {
            action = serviceOrder.getServiceType();
        }
        return ServiceType.valueOf(action.toUpperCase().trim());
    }
}
