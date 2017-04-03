package org.javaee.soap2rest.rest.web.utils;

import org.javaee.soap2rest.rest.impl.services.ResponseGeneratorServices;
import org.javaee.soap2rest.rest.web.RestRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.HttpURLConnection;

/**
 * Created by nikilipa on 3/31/17.
 */
@Provider
public class BadURIExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger log = LoggerFactory.getLogger(BadURIExceptionMapper.class);

    @Inject
    private ResponseGeneratorServices responseService;

    @Override
    public Response toResponse(NotFoundException nfe) {
        log.warn(nfe.getMessage());
        return Response
                .ok()
                .entity(responseService.getSimpleJsonError(
                        Integer.toString(HttpURLConnection.HTTP_BAD_REQUEST),
                        RestRegistration.RESOURCE_NOT_FOUND)
                )
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .build();
    }

}
