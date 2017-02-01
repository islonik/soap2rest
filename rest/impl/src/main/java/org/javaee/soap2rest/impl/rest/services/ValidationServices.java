package org.javaee.soap2rest.impl.rest.services;

import org.javaee.soap2rest.api.rest.model.AsyncRestRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * Created by nikilipa on 8/26/16.
 */
@ApplicationScoped
public class ValidationServices {

    @Inject
    private ResponseGeneratorServices responseServices;

    public Optional<Response> validAsyncRestRequest(AsyncRestRequest asyncRestRequest) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AsyncRestRequest>> violations = validator.validate(asyncRestRequest);

        if (!violations.isEmpty()) {
            Iterator<ConstraintViolation<AsyncRestRequest>> iterator = violations.iterator();
            StringBuilder badCases = new StringBuilder();
            badCases.append("Validation failed.");
            while (iterator.hasNext()) {
                ConstraintViolation<AsyncRestRequest> badCase = iterator.next();
                badCases.append(String.format("\n\tParam '%s' '%s'", badCase.getPropertyPath(), badCase.getMessage()));
            }
            return Optional.of(
                    Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(responseServices.getSimpleJsonError(
                            Response.Status.BAD_REQUEST.toString(),
                            badCases.toString()
                    ))
                    .build()
            );
        }
        return Optional.empty();
    }
}
