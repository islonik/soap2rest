package org.javaee.soap2rest.rest.web.utils;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;

import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EjbExceptionHandler implements ExceptionMapper<EJBException> {

    @Inject
    private ResponseGeneratorServices responseService;

    @Override
    public Response toResponse(EJBException ejbException) {
        String code = null;
        String desc = null;
        if (ejbException instanceof EJBAccessException) {
            code = Response.Status.UNAUTHORIZED.toString();
            desc = "Authorization failed.";
        } else {
            code = Response.Status.INTERNAL_SERVER_ERROR.toString();
            desc = ejbException.getMessage();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(
                        responseService.getSimpleJsonError(code, desc)
                )
                .build();
    }
}
