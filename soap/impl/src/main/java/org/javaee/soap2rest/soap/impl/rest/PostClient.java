package org.javaee.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by nikilipa on 8/13/16.
 */
@Dependent
public class PostClient implements ChangeClient {

    private final Logger log = LoggerFactory.getLogger(PostClient.class);

    public String send(String user, String pass, String endpoint, Entity body) {
        Client jaxrsClient = ClientBuilder.newClient().register(
                new Authenticator(user, pass)
        );

        log.info(String.format("POST request to S2R.rest:%n%s%n%s", endpoint, body.getEntity()));

        Response response = jaxrsClient
                .target(endpoint)
                .request(MediaType.APPLICATION_JSON)
                .post(body);

        // For Jersey 2.x use
        String result = response.readEntity(String.class);

        log.info(String.format("POST response from S2R.rest:%n%s", result));

        jaxrsClient.close();

        return result;
    }
}
