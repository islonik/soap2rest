package org.javaee.soap2rest.soap.impl.logic;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;

/**
 * Created by nikilipa on 2/16/17.
 */
public interface FastLogic {

    String METHOD = "fast";

    ServiceOrderStatus fast(Service service);

}
