package org.javaee.soap2rest.rest.web.utils;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.web.RestRegistration;

import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.HttpURLConnection;

@Provider
public class EjbExceptionMapper implements ExceptionMapper<EJBException> {

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
                .ok()
                .entity(responseService.getSimpleJsonError(code, desc))
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .build();
    }
}
