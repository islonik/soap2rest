package org.javaee.soap2rest.soap.impl.logic;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;

/**
 * Created by nikilipa on 2/15/17.
 */
public interface CamelInvoker {

    String METHOD = "invoke";

    ServiceOrderStatus invoke(Service service);
}
