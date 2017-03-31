package org.javaee.soap2rest.rest.web.utils;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by nikilipa on 3/31/17.
 */
@Provider
public class BadURIExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Inject
    private ResponseGeneratorServices responseService;

    @Override
    public Response toResponse(NotFoundException ejbException) {
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        responseService.getSimpleJsonError(
                                "Resource doesn't exist",
                                ejbException.getMessage()
                        )
                )
                .build();
    }

}
