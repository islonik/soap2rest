package org.spring.soap2rest.soap.impl.logic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.generated.ds.ws.StatusType;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 3/31/17.
 */
public class MulticastLogicTest {

    @Test
    public void testChooseBetweenEntitiesCase01() {
        ServiceOrderStatus sos = MulticastLogic.chooseBetweenEntities(null);
        Assert.assertEquals("500", sos.getStatusType().getCode());
        Assert.assertEquals("Result list is null or empty", sos.getStatusType().getDesc());
    }

    @Test
    public void testChooseBetweenEntitiesCase02() {
        ServiceOrderStatus sos = MulticastLogic.chooseBetweenEntities(Collections.emptyList());
        Assert.assertEquals("500", sos.getStatusType().getCode());
        Assert.assertEquals("Result list is null or empty", sos.getStatusType().getDesc());
    }

    @Test
    public void testChooseBetweenEntitiesCase03() {
        List<Future<ServiceOrderStatus>> resultList = new ArrayList<>();

        ServiceOrderStatus sos1 = new ServiceOrderStatus();
        StatusType statusType1 = new StatusType();
        statusType1.setCode("0");
        sos1.setStatusType(statusType1);
        resultList.add(new AsyncResult(sos1));

        ServiceOrderStatus sos2 = new ServiceOrderStatus();
        StatusType statusType2 = new StatusType();
        statusType2.setCode("0");
        sos2.setStatusType(statusType2);
        resultList.add(new AsyncResult(sos2));

        ServiceOrderStatus sos3 = new ServiceOrderStatus();
        StatusType statusType3 = new StatusType();
        statusType3.setCode("0");
        sos3.setStatusType(statusType3);
        resultList.add(new AsyncResult(sos3));

        ServiceOrderStatus sos = MulticastLogic.chooseBetweenEntities(resultList);
        Assert.assertEquals("0", sos.getStatusType().getCode());
    }

    @Test
    public void testChooseBetweenEntitiesCase04() {
        List<Future<ServiceOrderStatus>> resultList = new ArrayList<>();

        ServiceOrderStatus sos1 = new ServiceOrderStatus();
        StatusType statusType1 = new StatusType();
        statusType1.setCode("504");
        statusType1.setDesc("Gateway");
        sos1.setStatusType(statusType1);
        resultList.add(new AsyncResult(sos1));

        ServiceOrderStatus sos2 = new ServiceOrderStatus();
        StatusType statusType2 = new StatusType();
        statusType2.setCode("400");
        statusType2.setDesc("Bad request");
        sos2.setStatusType(statusType2);
        resultList.add(new AsyncResult(sos2));

        ServiceOrderStatus sos3 = new ServiceOrderStatus();
        StatusType statusType3 = new StatusType();
        statusType3.setCode("0");
        sos3.setStatusType(statusType3);
        resultList.add(new AsyncResult(sos3));

        ServiceOrderStatus sos = MulticastLogic.chooseBetweenEntities(resultList);
        Assert.assertEquals("504", sos.getStatusType().getCode());
        Assert.assertEquals("Gateway", sos.getStatusType().getDesc());
    }

    @Test
    public void testChooseBetweenEntitiesCase05() throws Exception {
        List<Future<ServiceOrderStatus>> resultList = new ArrayList<>();

        ServiceOrderStatus sos1 = new ServiceOrderStatus();
        StatusType statusType1 = new StatusType();
        statusType1.setCode("504");
        statusType1.setDesc("Gateway");
        sos1.setStatusType(statusType1);
        resultList.add(new AsyncResult(sos1));

        ServiceOrderStatus sos2 = new ServiceOrderStatus();
        StatusType statusType2 = new StatusType();
        statusType2.setCode("400");
        statusType2.setDesc("Bad request");
        sos2.setStatusType(statusType2);
        resultList.add(new AsyncResult(sos2));

        Future<ServiceOrderStatus> future = Mockito.mock(Future.class);
        Mockito.when(future.get(MulticastLogic.MULTICAST_TIMEOUT, TimeUnit.MILLISECONDS)).thenThrow(new TimeoutException());
        resultList.add(future);

        ServiceOrderStatus sos = MulticastLogic.chooseBetweenEntities(resultList);
        Assert.assertEquals("504", sos.getStatusType().getCode());
        Assert.assertEquals("Multicast timeout", sos.getStatusType().getDesc());
    }
}
