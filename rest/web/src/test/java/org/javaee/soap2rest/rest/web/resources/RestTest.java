package org.javaee.soap2rest.rest.web.resources;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.javaee.soap2rest.rest.impl.model.Message;
import org.javaee.soap2rest.rest.impl.services.MessageServices;
import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.impl.services.ValidationServices;
import org.javaee.soap2rest.rest.web.WildFlyResources;
import org.javaee.soap2rest.rest.web.utils.ExceptionHandler;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.mockito.Mockito;

import javax.inject.Singleton;
import javax.ws.rs.core.Application;
import java.util.concurrent.Executors;

/**
 * Created by nikilipa on 4/3/17.
 */
public class RestTest extends JerseyTest {

    @Override
    protected Application configure() {
        WildFlyResources wildFlyResources = new WildFlyResources();
        wildFlyResources.setExecutor(Executors.newCachedThreadPool());

        MessageServices messageServices = Mockito.mock(MessageServices.class);
        Message message = new Message();
        message.setId(1L);
        message.setMessage("Test Message");
        Mockito.when(messageServices.getRandomMessage()).thenReturn(message);

        ResourceConfig resourceConfig = new ResourceConfig(SyncResource.class, AsyncResource.class);

        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(JsonServices.class).to(JsonServices.class).in(Singleton.class);
                bind(ResponseGeneratorServices.class).to(ResponseGeneratorServices.class).in(Singleton.class);
                bind(ValidationServices.class).to(ValidationServices.class).in(Singleton.class);
                bind(messageServices).to(MessageServices.class);

                bind(wildFlyResources).to(WildFlyResources.class);

                bind(ExceptionHandler.class).to(ExceptionHandler.class);
            }
        });

        return resourceConfig;
    }

}
