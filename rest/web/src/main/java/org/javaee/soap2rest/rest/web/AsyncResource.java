package org.javaee.soap2rest.rest.web;

import org.javaee.soap2rest.rest.api.model.AsyncRestRequest;
import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.impl.services.ValidationServices;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.javaee.soap2rest.rest.web.utils.LoggerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Inject
    private WildFlyResources wildFlyResources;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{subResources:.*}")
    public Response getAbort() {
        return Response
                .ok(responseGeneratorServices.getSimpleJsonError(
                        Integer.toString(HttpURLConnection.HTTP_BAD_REQUEST),
                        "Resource doesn't exist")
                )
                .build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/about")
    public Response getAbout() {
        return Response
                .ok("Async Realm v1\n")
                .build();
    }

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

    private void asyncExecute(final AsyncResponse asyncResponse, final Supplier<Response> supplier) {
        CompletableFuture
            .supplyAsync(supplier, wildFlyResources.getExecutor())
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

     curl -X GET http://localhost:8078/soap2rest/rest/v1/async/response --user restadmin:restadmin

     */

    // http://localhost:8078/soap2rest/rest/v1/async/response
    @GET
    @Path("/response")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public void getResponse(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            @Suspended final AsyncResponse asyncResponse) {
        setUpTimeout(asyncResponse, TIMEOUT);

        asyncExecute(asyncResponse, () -> {
            log.warn(String.format("Async GET request was accepted."));

            return Response
                    .ok()
                    .entity(responseGeneratorServices.getRandomResponse())
                    .build();
        });
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

        asyncExecute(asyncResponse, () -> {
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
