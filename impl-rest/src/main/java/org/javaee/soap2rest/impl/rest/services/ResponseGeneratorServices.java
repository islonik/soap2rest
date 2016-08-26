package org.javaee.soap2rest.impl.rest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.javaee.soap2rest.api.rest.model.ErrorType;
import org.javaee.soap2rest.api.rest.model.RestResponse;
import org.javaee.soap2rest.utils.services.JsonService;
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

    @Inject
    private JsonService jsonService;

    public String getErrorResponse(String code, String message) {
        RestResponse restResponse = new RestResponse();
        ErrorType errorType = new ErrorType();
        errorType.setCode(code);
        errorType.setMessage(message);
        restResponse.setError(errorType);
        try {
            return jsonService.objectToJson(restResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return getSimpleJsonError(e);
        }
    }

    public String getRandomResponse() {
        try {
            RestResponse restResponse = new RestResponse();
            int rand = new Random().nextInt(10);

            if (rand >= 5) {
                int code = new Random().nextInt(500) + 100;
                int messageCode = new Random().nextInt(12);
                String message = null;
                switch (messageCode) {
                    case 0:
                        message = "Java in your eyes!";
                        break;
                    case 1:
                        message = "SQL injections in your mind!";
                        break;
                    case 2:
                        message = "Black magic of Java";
                        break;
                    case 4:
                        message = "Let objects rocks: live fast, die young";
                        break;
                    case 5:
                        message = "MAKING SQL QUERIES IN CAPITAL LETTERS MAKES DATABASE TO SENSE URGENCY AND RUN FASTER";
                        break;
                    case 6:
                        message = "Java 1 was of the 90's. Java 5 generics came from the 70's. Java 8 lambdas come from the 40's. Java 20 will have a steam regulator.";
                        break;
                    case 7:
                        message = "J2ME: Write once - debug everywhere.";
                        break;
                    case 8:
                        message = "BAKA!!!";
                        break;
                    case 9:
                        message = "Make jar not war";
                        break;
                    case 10:
                        message = "Code in Java: Be an enterprise hipster.";
                        break;
                    case 11:
                        message = "Observation: when your language is fucked up, and linked lists are the only simple data structure, you start to believe hash tables are bad.";
                        break;
                    case 12:
                        message = "I'm the best, fuck the rest!";
                        break;
                    default:
                        message = "Unknown message";
                }
                ErrorType error = new ErrorType();
                error.setCode(Integer.toString(code));
                error.setMessage(message);

                restResponse.setError(error);

                return jsonService.objectToJson(restResponse);
            }
            restResponse.setStatus("SUCCESS");
            return jsonService.objectToJson(restResponse);
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
                "{\n" +
                        "  \"error\" : {\n" +
                        "    \"code\" : \"%s\",\n" +
                        "    \"message\" : \"%s\"\n" +
                        "  }\n" +
                        "}",
                code,
                desc
        );
    }

    public String getAckResponse() {
        return String.format(
                "{\n" +
                        "    \"status\" : \"%s\"\n" +
                        "}",
                "Acknowledgement"
        );
    }
}
