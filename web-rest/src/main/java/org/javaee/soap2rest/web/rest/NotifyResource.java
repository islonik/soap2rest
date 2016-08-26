package org.javaee.soap2rest.web.rest;

import org.javaee.soap2rest.impl.rest.services.ResponseGeneratorServices;
import org.javaee.soap2rest.web.rest.utils.LoggerInterceptor;
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
@Path("/")
@RolesAllowed({
        RestRoles.REST_ROLE
})
public class NotifyResource {
    private static final Logger log = LoggerFactory.getLogger(AsyncEndpointResource.class);

    @Inject
    private ResponseGeneratorServices responseGeneratorServices;

    // http://localhost:8080/soap2rest/rest/v1/notify
    @POST
    @Path("/notify")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public Response notify(
            @Context HttpServletRequest httpRequest,
            Map<String, String> object) {

        log.warn(String.format("Notify request was accepted. Map content = %s", object.toString()));
        return Response.ok().entity(responseGeneratorServices.getRandomResponse()).build();
    }

}
