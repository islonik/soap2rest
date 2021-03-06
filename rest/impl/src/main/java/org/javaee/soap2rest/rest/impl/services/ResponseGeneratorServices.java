package org.javaee.soap2rest.rest.impl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.javaee.soap2rest.rest.api.model.ErrorType;
import org.javaee.soap2rest.rest.api.model.RestResponse;
import org.javaee.soap2rest.rest.impl.model.Message;
import org.javaee.soap2rest.utils.services.JsonServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Random;

/**
 * Created by nikilipa on 8/13/16.
 */
@ApplicationScoped
public class ResponseGeneratorServices {

    private static final Logger log = LoggerFactory.getLogger(ResponseGeneratorServices.class);

    public static final String TIMEOUT_MESSAGE = "Gateway timeout";

    @Inject
    private JsonServices jsonServices;

    @Inject
    private MessageServices messageServices;

    public String getErrorResponse(String code, String message) {
        RestResponse restResponse = new RestResponse();
        ErrorType errorType = new ErrorType();
        errorType.setCode(code);
        errorType.setMessage(message);
        restResponse.setError(errorType);
        try {
            return jsonServices.objectToJson(restResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return getSimpleJsonError(e);
        }
    }

    public String getRandomDatabaseResponse() {
        Message message = messageServices.getRandomMessage();
        return getErrorResponse(message.getId().toString(), message.getMessage());
    }

    public String getRandomResponse() {
        try {
            RestResponse restResponse = new RestResponse();
            int rand = new Random().nextInt(10);

            if (rand >= 5) {
                return getRandomDatabaseResponse();
            }
            restResponse.setStatus("SUCCESS");
            return jsonServices.objectToJson(restResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return getSimpleJsonError(e);
        }
    }

    public String getSuccessResponse() {
        try {
            RestResponse restResponse = new RestResponse();
            restResponse.setStatus("SUCCESS");
            return jsonServices.objectToJson(restResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return getSimpleJsonError(e);
        }
    }

    public String getSimpleJsonError(Exception e) {
        return getSimpleJsonError("500", e.getMessage());
    }

    public String getSimpleJsonError(String code, String desc) {
        return String.format(
                "{%n" +
                        "  \"error\" : {%n" +
                        "    \"code\" : \"%s\",%n" +
                        "    \"message\" : \"%s\"%n" +
                        "  }%n" +
                        "}",
                code,
                desc
        );
    }

    public String getAckResponse() {
        return String.format("{%n\t\"status\" : \"%s\"%n}", "Acknowledgement");
    }
}
