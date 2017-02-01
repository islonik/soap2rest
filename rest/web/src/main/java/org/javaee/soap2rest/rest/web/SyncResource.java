package org.javaee.soap2rest.rest.web;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
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

    /**

     curl -X POST -H "Content-Type: application/json" -d '{
        "test44" : "test55"
     }' http://localhost:8078/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */

    // http://localhost:8080/soap2rest/rest/v1/sync/response
    @POST
    @Path("/response")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public Response response(
            @Context HttpServletRequest httpRequest, // LoggerInterceptor
            Map<String, String> object) {

        log.warn(String.format("Sync request was accepted. Map content = %s", object.toString()));
        return Response.ok().entity(responseGeneratorServices.getRandomResponse()).build();
    }

}
