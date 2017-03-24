package org.javaee.soap2rest.rest.web;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.impl.services.ValidationServices;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nikilipa on 3/24/17.
 */
public class AsyncResourceTest extends JerseyTest  {

    private static final Logger log = LoggerFactory.getLogger(AsyncResourceTest.class);

    @Override
    protected Application configure() {
        WildFlyResources wildFlyResources = new WildFlyResources();
        wildFlyResources.setExecutor(Executors.newCachedThreadPool());

        ResourceConfig resourceConfig =  new ResourceConfig(AsyncResource.class);

        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(JsonServices.class).to(JsonServices.class).in(Singleton.class);
                bind(ResponseGeneratorServices.class).to(ResponseGeneratorServices.class).in(Singleton.class);
                bind(ValidationServices.class).to(ValidationServices.class).in(Singleton.class);

                bind(wildFlyResources).to(WildFlyResources.class);
            }
        });

        return resourceConfig;
    }

    @Test
    public void testNQPResourceOkCase01() throws Exception {
        log.warn("--- Timeout Start");
        final String testTimeout = new String(Files.readAllBytes(Paths.get("src/test/resources/timeout.json")));
        String response1 = target("async/timeout/1").request().get(String.class);
        Assert.assertEquals(testTimeout, response1);
        String response2 = target("async/timeout/2").request().get(String.class);
        Assert.assertEquals(testTimeout, response2);
        String response3 = target("async/timeout/3").request().get(String.class);
        Assert.assertEquals(testTimeout, response3);
    }

}
