package org.javaee.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by nikilipa on 2/15/17.
 */
@Dependent
public class GetClient {

    private final Logger log = LoggerFactory.getLogger(GetClient.class);

    public String get(String user, String password, String urlPath) {
        Client jaxrsClient = ClientBuilder.newClient().register(
                new Authenticator(user, password)
        );

        log.info(String.format("GET request to S2R.rest:%n%s", urlPath));

        Invocation.Builder builder = jaxrsClient
                .target(urlPath)
                .request(MediaType.APPLICATION_JSON);

        Response response = builder.get();

        // For Jersey 2.x use
        String result = response.readEntity(String.class);

        log.info(String.format("GET response from S2R.rest:%n%s", result));

        jaxrsClient.close();

        return result;
    }

}
