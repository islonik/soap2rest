package org.javaee.soap2rest.rest.web.resources;

import org.javaee.soap2rest.rest.web.RestRegistration;
import org.javaee.soap2rest.rest.web.RestRoles;
import org.javaee.soap2rest.rest.web.WildFlyResources;
import org.javaee.soap2rest.rest.web.model.AsyncInterrupter;
import org.javaee.soap2rest.rest.api.model.AsyncRestRequest;
import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.impl.services.ValidationServices;
import org.javaee.soap2rest.rest.web.utils.LoggerInterceptor;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Optional;
import java.util.concurrent.Future;
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
    private ResponseGeneratorServices responseGeneratorServices;

    @Inject
    private ValidationServices validationServices;

    @Inject
    private WildFlyResources wildFlyResources;

    // This is an example how you can manage regexp in WildFly10, but in real application it is bad practice. You should use ExceptionMapper instead of it.
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    //@Path("{subResources:.*}") - we can't use this expression in new resteasy because: RESTEASY002142
//    @Path("{subResources:(?!(about)|(response)|(notify)|(timeout))(.*)}")
//    public Response getAbort() {
//        log.info("getAbort");
//        return Response
//                .ok(responseGeneratorServices.getSimpleJsonError(
//                        Integer.toString(HttpURLConnection.HTTP_BAD_REQUEST),
//                        RestRegistration.RESOURCE_NOT_FOUND)
//                )
//                .build();
//    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/about/")
    public Response getAbout() {
        log.info("getAbout");
        return Response
                .ok("Async Realm v1\n")
                .build();
    }

    private void asyncExecute(final AsyncResponse asyncResponse, final Supplier<Response> supplier) {
        final AsyncInterrupter asyncInterrupter = new AsyncInterrupter();

        asyncResponse.setTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        asyncResponse.setTimeoutHandler(
                asyncResp -> {
                    Future task = asyncInterrupter.getTask();
                    if (task != null) {
                        task.cancel(true);
                    }
                    asyncResp.resume(responseGeneratorServices.getErrorResponse(
                            Integer.toString(HttpURLConnection.HTTP_GATEWAY_TIMEOUT),
                            ResponseGeneratorServices.TIMEOUT_MESSAGE
                    ));
                }
        );

        asyncInterrupter.setFuture(wildFlyResources.getExecutor().submit(() -> {
            try {
                asyncResponse.resume(supplier.get());
            } catch (Throwable t) {
                asyncResponse.resume(responseGeneratorServices.getErrorResponse(
                        "500", t.getMessage()
                ));
            }
        }));
    }

    /**
     * curl -X GET http://localhost:8078/soap2rest/rest/v1/async/response --user restadmin:restadmin
     */

    // http://localhost:8078/soap2rest/rest/v1/async/response
    @GET
    @Path("/response/")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public void getResponse(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            @Suspended final AsyncResponse asyncResponse) {

        asyncExecute(asyncResponse, () -> {
            log.warn(String.format("Async GET request was accepted."));

            return Response
                    .ok()
                    .entity(responseGeneratorServices.getRandomResponse())
                    .build();
        });
    }

    /**
     * curl -X PUT -H "Content-Type: application/json" -d '{
     * "messageId" : "test11",
     * "conversationId" : "test22",
     * "code" : "110",
     * "desc" : "J2ME: Write once - debug everywhere."
     * }' http://localhost:8078/soap2rest/rest/v1/async/notify --user restadmin:restadmin
     */
    // http://localhost:8080/soap2rest/rest/v1/async/notify
    @PUT
    @Path("/notify/")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public void notify(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            AsyncRestRequest asyncRestRequest,
            @Suspended final AsyncResponse asyncResponse) {

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

    @GET
    @Path("/timeout/{id}/")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public void timeout(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            @PathParam("id") String id,
            @Suspended final AsyncResponse asyncResponse) {

        asyncExecute(asyncResponse, () -> {
            log.info(String.format("Async request id = %s is running!", id));
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException ie) {
                log.warn(String.format("Async request was interrupted during timeout."));
            }

            return Response.ok().entity(responseGeneratorServices.getAckResponse()).build();
        });
    }


}
