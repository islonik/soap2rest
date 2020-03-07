package org.javaee.soap2rest.soap.impl;

import com.google.common.collect.Lists;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.javaee.soap2rest.soap.impl.camel.CamelStarter;
import org.javaee.soap2rest.soap.impl.dao.AuthUserDao;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.logic.AsyncLogic;
import org.javaee.soap2rest.soap.impl.logic.MulticastLogic;
import org.javaee.soap2rest.soap.impl.logic.SyncLogic;
import org.javaee.soap2rest.soap.impl.model.sql.AuthUser;
import org.javaee.soap2rest.soap.impl.rest.GetClient;
import org.javaee.soap2rest.soap.impl.rest.PostClient;
import org.javaee.soap2rest.soap.impl.rest.PutClient;
import org.javaee.soap2rest.soap.impl.routes.ExceptionRoute;
import org.javaee.soap2rest.soap.impl.routes.fast.FastAsyncRoute;
import org.javaee.soap2rest.soap.impl.routes.fast.FastMulticastRoute;
import org.javaee.soap2rest.soap.impl.routes.fast.FastSyncRoute;
import org.javaee.soap2rest.soap.impl.routes.medium.MediumAsyncRoute;
import org.javaee.soap2rest.soap.impl.routes.medium.MediumMulticastRoute;
import org.javaee.soap2rest.soap.impl.routes.medium.MediumSyncRoute;
import org.javaee.soap2rest.soap.impl.routes.slow.SlowAsyncRoute;
import org.javaee.soap2rest.soap.impl.routes.slow.SlowMulticastRoute;
import org.javaee.soap2rest.soap.impl.routes.slow.SlowSyncRoute;
import org.javaee.soap2rest.soap.impl.services.JaxbServices;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.javaee.soap2rest.soap.impl.services.RestServices;
import org.javaee.soap2rest.soap.impl.services.sql.DbAuthServices;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Created by nikilipa on 2/15/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CamelManagerTest extends CamelTestSupport {

    protected JaxbServices jaxbServices = ((Supplier<JaxbServices>) () -> {
        try {
            return new JaxbServices();
        } catch (Exception e) {
            System.exit(1);
            return null;
        }
    }).get();

    private WildFlyResources wildFlyResources;
    private CamelStarter camelStarter;
    private JsonServices jsonServices;
    private ParserServices parserServices;
    private DbAuthServices dbAuthServices;

    private GetClient getClient;
    private PutClient putClient;
    private PostClient postClient;
    private RestServices restServices;

    private ExecutorService executor;

    @Override
    public RoutesBuilder[] createRouteBuilders() {
        wildFlyResources = Mockito.mock(WildFlyResources.class);
        executor = Executors.newFixedThreadPool(20);
        Mockito.when(wildFlyResources.getExecutor()).thenReturn(executor);

        this.camelStarter = new CamelStarter(this.context(), this.template(), wildFlyResources);

        this.jsonServices = new JsonServices();
        this.parserServices = new ParserServices();

        AuthUserDao authUserDao = Mockito.mock(AuthUserDao.class);
        this.dbAuthServices = new DbAuthServices();
        this.dbAuthServices.setAuthUserDao(authUserDao);

        AuthUser authUser = new AuthUser();
        authUser.setService("default");
        authUser.setUsername("restadmin");
        authUser.setPassphrase("restadmin");

        Mockito.when(authUserDao.findAllUsers()).thenReturn(Lists.newArrayList(authUser));
        dbAuthServices.updateCache();

        this.getClient = Mockito.mock(GetClient.class);
        this.putClient = Mockito.mock(PutClient.class);
        this.postClient = Mockito.mock(PostClient.class);

        this.restServices = new RestServices();
        this.restServices.setRestHost("http://localhost:8078/soap2rest/rest/v1");
        this.restServices.setGetClient(getClient);
        this.restServices.setPutClient(putClient);
        this.restServices.setPostClient(postClient);
        this.restServices.setJsonServices(jsonServices);
        this.restServices.setParserServices(parserServices);
        this.restServices.setDbAuthServices(dbAuthServices);

        ExceptionRoute exceptionRoute = new ExceptionRoute();

        // AsyncLogic
        AsyncLogic asyncLogic = new AsyncLogic();
        asyncLogic.setJsonServices(jsonServices);
        asyncLogic.setParserServices(parserServices);
        asyncLogic.setRestServices(restServices);

        FastAsyncRoute fastAsyncRoute = new FastAsyncRoute();
        fastAsyncRoute.setAsyncLogic(asyncLogic);

        MediumAsyncRoute mediumAsyncRoute = new MediumAsyncRoute();
        mediumAsyncRoute.setAsyncLogic(asyncLogic);

        SlowAsyncRoute slowAsyncRoute = new SlowAsyncRoute();
        slowAsyncRoute.setAsyncLogic(asyncLogic);

        // MulticastLogic
        MulticastLogic multicastLogic = new MulticastLogic();
        multicastLogic.setJsonServices(jsonServices);
        multicastLogic.setParserServices(parserServices);
        multicastLogic.setRestServices(restServices);

        FastMulticastRoute fastMulticastRoute = new FastMulticastRoute();
        fastMulticastRoute.setMulticastLogic(multicastLogic);
        fastMulticastRoute.setJsonServices(jsonServices);
        fastMulticastRoute.setRestServices(restServices);
        fastMulticastRoute.setExecutor(executor);

        MediumMulticastRoute mediumMulticastRoute = new MediumMulticastRoute();
        mediumMulticastRoute.setMulticastLogic(multicastLogic);
        mediumMulticastRoute.setJsonServices(jsonServices);
        mediumMulticastRoute.setRestServices(restServices);
        mediumMulticastRoute.setExecutor(executor);

        SlowMulticastRoute slowMulticastRoute = new SlowMulticastRoute();
        slowMulticastRoute.setMulticastLogic(multicastLogic);
        slowMulticastRoute.setJsonServices(jsonServices);
        slowMulticastRoute.setRestServices(restServices);
        slowMulticastRoute.setExecutor(executor);

        // SyncLogic
        SyncLogic syncLogic = new SyncLogic();
        syncLogic.setJsonServices(jsonServices);
        syncLogic.setParserServices(parserServices);
        syncLogic.setRestServices(restServices);

        FastSyncRoute fastSyncRoute = new FastSyncRoute();
        fastSyncRoute.setSyncLogic(syncLogic);

        MediumSyncRoute mediumSyncRoute = new MediumSyncRoute();
        mediumSyncRoute.setSyncLogic(syncLogic);

        SlowSyncRoute slowSyncRoute = new SlowSyncRoute();
        slowSyncRoute.setSyncLogic(syncLogic);

        return new RouteBuilder[]{
                exceptionRoute,

                fastAsyncRoute,
                mediumAsyncRoute,
                slowAsyncRoute,

                fastMulticastRoute,
                mediumMulticastRoute,
                slowMulticastRoute,

                fastSyncRoute,
                mediumSyncRoute,
                slowSyncRoute
        };
    }

    @Test
    public void testExceptionRouteCase01() throws Exception {
        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/exception/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("unknown-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("unknown-conversation-case01", dsResponse.getHeader().getConversationId());
        Assert.assertEquals("OrderUnknown01", dsResponse.getBody().getServiceOrderStatus().getServiceOrderID());
        Assert.assertEquals("400", dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        Assert.assertEquals("Service 'Unknown' is not implemented yet!", dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());
    }

    @Test
    public void testExceptionRouteCase02() throws Exception {
        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/exception/Case02.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("unknown-message-case02", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("unknown-conversation-case02", dsResponse.getHeader().getConversationId());
        Assert.assertEquals("OrderUnknown02", dsResponse.getBody().getServiceOrderStatus().getServiceOrderID());
        Assert.assertEquals("400", dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        Assert.assertEquals("No enum constant org.javaee.soap2rest.soap.impl.model.ServiceType.UNKNOWN", dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());
    }

    @Test
    public void testAsyncRouteCase01() throws Exception {
        Assert.assertTrue("slow", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/async/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("async-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("async-conversation-case01", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderAsync01", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance fast measure:"));
    }

    @Test
    public void testAsyncRouteCase02() throws Exception {
        Assert.assertTrue("medium", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/async/Case02.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse, jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("async-message-case02", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("async-conversation-case02", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderAsync02", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance medium measure:"));
    }

    @Test
    public void testAsyncRouteCase03() throws Exception {
        Assert.assertTrue("fast", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/async/Case03.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse, jsonResponse, jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("async-message-case03", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("async-conversation-case03", dsResponse.getHeader().getConversationId());
        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderAsync03", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance slow measure:"));
    }

    @Test
    public void testMulticastRouteCase01() throws Exception {
        Assert.assertTrue("fast", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/multicast/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse);
        Mockito.when(putClient.send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(jsonResponse);
        Mockito.when(postClient.send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("multicast-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("multicast-conversation-case01", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderMulticast01", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance fast measure:"));
    }

    @Test
    public void testMulticastRouteCase02() throws Exception {
        Assert.assertTrue("medium", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/multicast/Case02.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse);
        Mockito.when(putClient.send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(jsonResponse);
        Mockito.when(postClient.send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("multicast-message-case02", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("multicast-conversation-case02", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderMulticast02", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance medium measure:"));
    }

    @Test
    public void testMulticastRouteCase03() throws Exception {
        Assert.assertTrue("slow", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/multicast/Case03.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse);
        Mockito.when(putClient.send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(jsonResponse);
        Mockito.when(postClient.send(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("multicast-message-case03", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("multicast-conversation-case03", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderMulticast03", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance slow measure:"));
    }

    @Test
    public void testSyncRouteCase01() throws Exception {
        Assert.assertTrue("fast", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/sync/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("sync-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("sync-conversation-case01", dsResponse.getHeader().getConversationId());
        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderSync01", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance fast measure:"));
    }

    @Test
    public void testSyncRouteCase02() throws Exception {
        Assert.assertTrue("medium", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/sync/Case02.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse, jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("sync-message-case02", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("sync-conversation-case02", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderSync02", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance medium measure:"));
    }

    @Test
    public void testSyncRouteCase03() throws Exception {
        Assert.assertTrue("slow", true);

        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/sync/Case03.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse, jsonResponse, jsonResponse);

        CamelManager camelManager = new CamelManager(jsonServices, parserServices, camelStarter);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("sync-message-case03", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("sync-conversation-case03", dsResponse.getHeader().getConversationId());

        ServiceOrderStatus sos = dsResponse.getBody().getServiceOrderStatus();
        Assert.assertEquals("OrderSync03", sos.getServiceOrderID());
        Assert.assertEquals("0", sos.getStatusType().getCode());
        Assert.assertEquals("SUCCESS", sos.getStatusType().getDesc());
        Assert.assertTrue(sos.getStatusType().getResult().startsWith("Performance slow measure:"));
    }

}
