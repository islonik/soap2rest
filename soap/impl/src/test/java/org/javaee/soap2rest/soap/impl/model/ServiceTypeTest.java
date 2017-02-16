package org.javaee.soap2rest.soap.impl.model;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nikilipa on 2/16/17.
 */
public class ServiceTypeTest {

    @Test
    public void testValueOfCase01() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType("Fast");
        Assert.assertEquals(ServiceType.FAST, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase02() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType(" Fast");
        Assert.assertEquals(ServiceType.FAST, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase03() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType("Fast ");
        Assert.assertEquals(ServiceType.FAST, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase04() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType(" fast ");
        Assert.assertEquals(ServiceType.FAST, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase05() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType("Medium");
        Assert.assertEquals(ServiceType.MEDIUM, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase06() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType(" Medium");
        Assert.assertEquals(ServiceType.MEDIUM, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase07() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType("Medium ");
        Assert.assertEquals(ServiceType.MEDIUM, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase08() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType(" medium ");
        Assert.assertEquals(ServiceType.MEDIUM, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase09() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType("Slow");
        Assert.assertEquals(ServiceType.SLOW, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase10() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType(" Slow");
        Assert.assertEquals(ServiceType.SLOW, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase11() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType("Slow ");
        Assert.assertEquals(ServiceType.SLOW, ServiceType.valueOf(serviceOrder));
    }

    @Test
    public void testValueOfCase12() {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.setServiceType(" slow ");
        Assert.assertEquals(ServiceType.SLOW, ServiceType.valueOf(serviceOrder));
    }
}
