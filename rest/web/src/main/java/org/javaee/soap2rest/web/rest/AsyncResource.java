package org.javaee.soap2rest.web.rest;

import org.javaee.soap2rest.api.rest.model.AsyncRestRequest;
import org.javaee.soap2rest.impl.rest.services.ResponseGeneratorServices;
import org.javaee.soap2rest.impl.rest.services.ValidationServices;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.javaee.soap2rest.web.rest.utils.LoggerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by nikilipa on 7/19/16.
 */
@Stateless
@Path("/async")
@RolesAllowed({
        RestRoles.REST_ROLE
})
public class AsyncResource {

    private static final Logger log = LoggerFactory.getLogger(AsyncResource.class);

    private static final long TIMEOUT = 500; // ms

    @Inject
    private JsonServices jsonServices;

    @Inject
    private ValidationServices validationServices;

    @Inject
    private ResponseGeneratorServices responseGeneratorServices;

    @Resource(name = WildFlyConfigs.REST_EXECUTOR)
    private ExecutorService executor;

    private void setUpTimeout(final AsyncResponse asyncResponse, long timeout) {
        asyncResponse.setTimeout(timeout, TimeUnit.MILLISECONDS);
        asyncResponse.setTimeoutHandler(
            asyncResp -> asyncResp.resume(
                responseGeneratorServices.getErrorResponse(
                    Integer.toString(HttpURLConnection.HTTP_GATEWAY_TIMEOUT),
                    ResponseGeneratorServices.TIMEOUT_MESSAGE
                )
            )
        );
    }

    public void asyncExecute(final ExecutorService executor, final AsyncResponse asyncResponse, final Supplier<Response> supplier) {
        CompletableFuture
            .supplyAsync(supplier, executor)
            .thenApply(response -> asyncResponse.resume(response))
            .exceptionally(throwable ->
                asyncResponse.resume(
                    responseGeneratorServices.getErrorResponse(
                        "500", throwable.getMessage()
                    )
                )
            );
    }

    /**

     curl -X PUT -H "Content-Type: application/json" -d '{
     "messageId" : "test11",
     "conversationId" : "test22",
     "code" : "110",
     "desc" : "J2ME: Write once - debug everywhere."
     }' http://localhost:8078/soap2rest/rest/v1/async/notify --user restadmin:restadmin

     */
    // http://localhost:8080/soap2rest/rest/v1/async/notify
    @PUT
    @Path("/notify")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public void notify(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            AsyncRestRequest asyncRestRequest,
            @Suspended final AsyncResponse asyncResponse) {

        setUpTimeout(asyncResponse, TIMEOUT);

        asyncExecute(executor, asyncResponse, () -> {
            log.warn(String.format("Async request was accepted. %s", asyncRestRequest.toString()));

            Optional<Response> validResponse = validationServices.validAsyncRestRequest(asyncRestRequest);
            if (validResponse.isPresent()) {
                return validResponse.get();
            }

            log.info(String.format("Async request was validated. %s", asyncRestRequest.toString()));

            return Response.ok().entity(responseGeneratorServices.getAckResponse()).build();
        });
    }

}
