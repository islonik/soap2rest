package org.javaee.soap2rest.rest.web.resources;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.web.RestRegistration;
import org.javaee.soap2rest.rest.web.RestRoles;
import org.javaee.soap2rest.rest.web.utils.LoggerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by nikilipa on 8/13/16.
 */
@Stateless
@Path("/sync")
@RolesAllowed({
        RestRoles.REST_ROLE
})
public class SyncResource {

    private static final Logger log = LoggerFactory.getLogger(SyncResource.class);

    @Inject
    private ResponseGeneratorServices responseGeneratorServices;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    //@Path("{subResources:.*}") - we can't use this expression in new resteasy because: RESTEASY002142
    @Path("{subResources:(?!(about)|(response))(.*)}")
    public Response getAbort() {
        log.info("getAbort");
        return Response
                .ok(responseGeneratorServices.getSimpleJsonError(
                        Integer.toString(HttpURLConnection.HTTP_BAD_REQUEST),
                        RestRegistration.RESOURCE_NOT_FOUND)
                )
                .build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/about")
    public Response getAbout() {
        return Response
                .ok("Sync Realm v1\n")
                .build();
    }

    /**

     curl -X GET http://localhost:8078/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */

    // http://localhost:8078/soap2rest/rest/v1/sync/response
    @GET
    @Path("/response")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public Response getResponse(
            @Context HttpServletRequest httpRequest) {// LoggerInterceptor

        log.warn(String.format("Sync GET request was accepted."));
        return Response
                .ok()
                .entity(responseGeneratorServices.getRandomResponse())
                .build();
    }

    /**

     curl -X PUT http://localhost:8078/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */

    // http://localhost:8078/soap2rest/rest/v1/sync/response
    @PUT
    @Path("/response")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public Response putResponse(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            Map<String, String> object) {
        log.warn(String.format("Sync PUT request was accepted. Map content = %s", object.toString()));

        return Response
                .ok()
                .entity(responseGeneratorServices.getRandomResponse())
                .build();
    }

    /**

     curl -X POST -H "Content-Type: application/json" -d '{
        "test44" : "test55"
     }' http://localhost:8078/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */

    // http://localhost:8078/soap2rest/rest/v1/sync/response
    @POST
    @Path("/response")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public Response postResponse(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            Map<String, String> object) {
        log.warn(String.format("Sync POST request was accepted. Map content = %s", object.toString()));

        return Response
                .ok()
                .entity(responseGeneratorServices.getRandomResponse())
                .build();
    }

}
