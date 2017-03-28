package org.spring.soap2rest.rest.impl.services;

import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * Created by nikilipa on 8/26/16.
 */
@Service
public class ValidationServices {

    @Autowired
    private ResponseGeneratorServices responseServices;

    public Optional<ResponseEntity> validAsyncRestRequest(AsyncRestRequest asyncRestRequest) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AsyncRestRequest>> violations = validator.validate(asyncRestRequest);

        if (!violations.isEmpty()) {
            Iterator<ConstraintViolation<AsyncRestRequest>> iterator = violations.iterator();
            StringBuilder badCases = new StringBuilder();
            badCases.append("Validation failed.");
            while (iterator.hasNext()) {
                ConstraintViolation<AsyncRestRequest> badCase = iterator.next();
                badCases.append(String.format("%n\tParam '%s' '%s'", badCase.getPropertyPath(), badCase.getMessage()));
            }
            return Optional.of(
                    ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(responseServices.getSimpleJsonError(
                            HttpStatus.BAD_REQUEST.toString(),
                            badCases.toString()
                    ))
            );
        }
        return Optional.empty();
    }
}
