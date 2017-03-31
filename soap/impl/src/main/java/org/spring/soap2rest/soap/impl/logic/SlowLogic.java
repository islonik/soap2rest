package org.spring.soap2rest.soap.impl.logic;

import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.model.Service;

/**
 * Created by nikilipa on 2/16/17.
 */
public interface SlowLogic {

    String METHOD = "slow";

    ServiceOrderStatus slow(Service service);

}