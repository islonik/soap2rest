package org.javaee.soap2rest.rest.web.utils;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;

import javax.ejb.EJBAccessException;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Inject
    private ResponseGeneratorServices responseService;

    @Override
    public Response toResponse(Exception exception) {
        Response.StatusType statusType = getStatusType(exception);
        return Response
                .status(statusType)
                .type(MediaType.APPLICATION_JSON)
                .entity(responseService.getSimpleJsonError(
                        Integer.toString(statusType.getStatusCode()),
                        mapReasonPhrase(exception, statusType))
                )
                .build();
    }

    Response.StatusType getStatusType(Exception exception) {
        if (exception instanceof EJBAccessException) {
            return Response.Status.FORBIDDEN;
        } else if (exception instanceof NotFoundException) {
            return Response.Status.NOT_FOUND;
        } else {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
    }

    String mapReasonPhrase(Exception e, Response.StatusType statusType) {
        if (statusType.equals(Response.Status.INTERNAL_SERVER_ERROR)) {
            return e.getMessage();
        } else {
            return statusType.getReasonPhrase();
        }
    }

}
