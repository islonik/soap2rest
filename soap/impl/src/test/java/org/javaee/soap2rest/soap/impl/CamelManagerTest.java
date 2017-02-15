package org.javaee.soap2rest.soap.impl;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.javaee.soap2rest.rest.api.model.RestResponse;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.soap.impl.logic.AsyncLogic;
import org.javaee.soap2rest.soap.impl.logic.MulticastLogic;
import org.javaee.soap2rest.soap.impl.logic.SyncLogic;
import org.javaee.soap2rest.soap.impl.model.AuthUser;
import org.javaee.soap2rest.soap.impl.rest.GetClient;
import org.javaee.soap2rest.soap.impl.rest.PostClient;
import org.javaee.soap2rest.soap.impl.rest.PutClient;
import org.javaee.soap2rest.soap.impl.routes.AsyncRoute;
import org.javaee.soap2rest.soap.impl.routes.ExceptionRoute;
import org.javaee.soap2rest.soap.impl.routes.MulticastRoute;
import org.javaee.soap2rest.soap.impl.routes.SyncRoute;
import org.javaee.soap2rest.soap.impl.services.DbAuthServices;
import org.javaee.soap2rest.soap.impl.services.JaxbServices;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.javaee.soap2rest.soap.impl.services.RestServices;
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
        executor = Executors.newFixedThreadPool(20);

        this.camelStarter = new CamelStarter();
        this.camelStarter.setCamelContext(this.context());
        this.camelStarter.setTemplate(this.template());
        this.camelStarter.setExecutorService(executor);

        this.jsonServices = new JsonServices();
        this.parserServices = new ParserServices();
        this.dbAuthServices = Mockito.mock(DbAuthServices.class);
        AuthUser authUser = new AuthUser();
        authUser.setUser("restadmin");
        authUser.setPass("restadmin");
        Mockito.when(dbAuthServices.getUser()).thenReturn(authUser);

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

        AsyncLogic asyncLogic = new AsyncLogic();
        asyncLogic.setJsonServices(jsonServices);
        asyncLogic.setParserServices(parserServices);
        asyncLogic.setRestServices(restServices);

        AsyncRoute asyncRoute = new AsyncRoute();
        asyncRoute.setAsyncLogic(asyncLogic);

        ExceptionRoute exceptionRoute = new ExceptionRoute();

        MulticastLogic multicastLogic = new MulticastLogic();
        multicastLogic.setJsonServices(jsonServices);
        multicastLogic.setParserServices(parserServices);
        multicastLogic.setRestServices(restServices);

        MulticastRoute multicastRoute = new MulticastRoute();
        multicastRoute.setMulticastLogic(multicastLogic);
        multicastRoute.setJsonServices(jsonServices);
        multicastRoute.setRestServices(restServices);
        multicastRoute.setExecutor(executor);

        SyncLogic syncLogic = new SyncLogic();
        syncLogic.setJsonServices(jsonServices);
        syncLogic.setParserServices(parserServices);
        syncLogic.setRestServices(restServices);

        SyncRoute syncRoute = new SyncRoute();
        syncRoute.setSyncLogic(syncLogic);

        return new RouteBuilder[]{
                asyncRoute,
                exceptionRoute,
                multicastRoute,
                syncRoute
        };
    }

    @Test
    public void testAsyncRouteCase01() throws Exception {
        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/async/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse, jsonResponse, jsonResponse);

        CamelManager camelManager = new CamelManager(camelStarter, parserServices, restServices, jsonServices);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("async-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("async-conversation-case01", dsResponse.getHeader().getConversationId());
        Assert.assertEquals("OrderAsync01", dsResponse.getBody().getServiceOrderStatus().getServiceOrderID());
        Assert.assertEquals("0", dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        Assert.assertEquals("SUCCESS", dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());
    }

    @Test
    public void testExceptionRouteCase01() throws Exception {
        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/exception/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        CamelManager camelManager = new CamelManager(camelStarter, parserServices, restServices, jsonServices);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("UNKNOWN-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("UNKNOWN-conversation-case01", dsResponse.getHeader().getConversationId());
        Assert.assertEquals("OrderUNKNOWN01", dsResponse.getBody().getServiceOrderStatus().getServiceOrderID());
        Assert.assertEquals("400", dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        Assert.assertEquals("Service UNKNOWN is not implemented yet!", dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());
    }

    @Test
    public void testMulticastRouteCase01() throws Exception {
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

        CamelManager camelManager = new CamelManager(camelStarter, parserServices, restServices, jsonServices);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("multicast-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("multicast-conversation-case01", dsResponse.getHeader().getConversationId());
        Assert.assertEquals("OrderMulticast01", dsResponse.getBody().getServiceOrderStatus().getServiceOrderID());
        Assert.assertEquals("0", dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        Assert.assertEquals("SUCCESS", dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());
    }

    @Test
    public void testSyncRouteCase01() throws Exception {
        DSRequest dsRequest = null;
        String xmlResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/sync/Case01.xml")));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(xmlResponse.getBytes())) {
            SOAPMessage message = MessageFactory.newInstance().createMessage(null, bais);
            dsRequest = jaxbServices.getDsRequest(message);
        }

        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/responses/Ok.json")));

        Mockito.when(getClient.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(jsonResponse, jsonResponse, jsonResponse);

        CamelManager camelManager = new CamelManager(camelStarter, parserServices, restServices, jsonServices);

        DSResponse dsResponse = camelManager.syncProcess(dsRequest);

        Assert.assertNotNull(dsResponse);
        Assert.assertEquals("sync-message-case01", dsResponse.getHeader().getMessageId());
        Assert.assertEquals("sync-conversation-case01", dsResponse.getHeader().getConversationId());
        Assert.assertEquals("OrderSync01", dsResponse.getBody().getServiceOrderStatus().getServiceOrderID());
        Assert.assertEquals("0", dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        Assert.assertEquals("SUCCESS", dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());
    }

}
