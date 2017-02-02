package org.javaee.soap2rest.soap.impl.rest;

import org.javaee.soap2rest.utils.properties.S2RProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 8/13/16.
 */
@Dependent
public class PutClient {
    private final Logger log = LoggerFactory.getLogger(PutClient.class);

    private static final Long TIMEOUT = 10L;

    @Inject
    @S2RProperty("s2r.rest.host")
    private String restHost;

    public String getRestHost() {
        return restHost;
    }

    public String send(String user, String pass, String endpoint, Entity body)
            throws InterruptedException, ExecutionException, TimeoutException {
        Client jaxrsClient = ClientBuilder.newClient().register(
                new Authenticator(user, pass)
        );

        log.info(String.format("PUT request to S2R.rest:%n%s%n%s", endpoint, body.getEntity()));

        Response response = jaxrsClient
                .target(endpoint)
                .request(MediaType.APPLICATION_JSON)
                .async()
                .put(body)
                .get(TIMEOUT, TimeUnit.SECONDS);

        // For Jersey 2.x use
        String result = response.readEntity(String.class);

        log.info(String.format("PUT response from S2R.rest:%n%s", result));

        jaxrsClient.close();

        return result;
    }
}
