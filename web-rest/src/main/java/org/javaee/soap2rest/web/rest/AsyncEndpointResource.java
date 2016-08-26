package org.javaee.soap2rest.web.rest;

import org.javaee.soap2rest.api.rest.model.AsyncRestRequest;
import org.javaee.soap2rest.impl.rest.services.ResponseGeneratorServices;
import org.javaee.soap2rest.impl.rest.services.ValidationServices;
import org.javaee.soap2rest.web.rest.utils.LoggerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Created by nikilipa on 7/19/16.
 */
@Stateless
@Path("/")
@RolesAllowed({
        RestRoles.REST_ROLE
})
public class AsyncEndpointResource {
    private static final Logger log = LoggerFactory.getLogger(AsyncEndpointResource.class);

    @Inject
    private ValidationServices validationServices;

    @Inject
    private ResponseGeneratorServices responseGeneratorServices;

    // http://localhost:8080/soap2rest/rest/v1/response
    @PUT
    @Path("/response")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed(RestRoles.REST_ROLE)
    @Interceptors(LoggerInterceptor.class)
    public Response response(
            @Context HttpServletRequest httpRequest,
            AsyncRestRequest asyncRestRequest) {

        Optional<Response> validResponse = validationServices.validAsyncRestRequest(asyncRestRequest);
        if (validResponse.isPresent()) {
            return validResponse.get();
        }

        log.warn(String.format("Async request was accepted. %s", asyncRestRequest.toString()));
        return Response.ok().entity(responseGeneratorServices.getAckResponse()).build();
    }

}
