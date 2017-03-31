package org.spring.soap2rest.soap.impl.services;

import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.rest.api.model.RestResponse;
import org.spring.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.model.db.AuthUser;
import org.spring.soap2rest.soap.impl.rest.GetClient;
import org.spring.soap2rest.soap.impl.rest.PostClient;
import org.spring.soap2rest.soap.impl.rest.PutClient;
import org.spring.soap2rest.utils.services.JsonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by nikilipa on 8/13/16.
 */
@Component
public class RestServices {

    public static final String HTTP_TEMPLATE = "";

    @Autowired
    private JsonServices jsonServices;

    @Autowired
    private ParserServices parserServices;

    @Autowired
    private DbAuthServices dbAuthServices;

    @Autowired
    private GetClient getClient;

    @Autowired
    private PutClient putClient;

    @Autowired
    private PostClient postClient;

    @Value("${host}")
    private String path2rest;

    private String getPath2rest() {
        return path2rest;
    }

    public ServiceOrderStatus sendGetRequest(Service service, String urlTemplate) {
        AuthUser user = dbAuthServices.getUser(service.getName());
        String url = String.format(urlTemplate, getPath2rest());
        try {
            String getResponse = getClient.send(user.getUsername(), user.getPassphrase(), url);
            return parseHttpResponse(getResponse);
        } catch (IOException ioe) {
            return parserServices.createServiceOrderStatus(ParserServices.CODE_BUG, ioe.getMessage());
        }
    }

    public ServiceOrderStatus sendPostRequest(Service service, String urlTemplate, String body) {
        AuthUser user = dbAuthServices.getUser(service.getName());
        String endpoint = String.format(urlTemplate, getPath2rest());
        try {
            String getResponse = postClient.send(user.getUsername(), user.getPassphrase(), endpoint, body);
            return parseHttpResponse(getResponse);
        } catch (IOException ioe) {
            return parserServices.createServiceOrderStatus(ParserServices.CODE_BUG, ioe.getMessage());
        }
    }

    public ServiceOrderStatus sendPutRequest(Service service, String urlTemplate, AsyncRestRequest body) {
        AuthUser user = dbAuthServices.getUser(service.getName());
        String endpoint = String.format(urlTemplate, getPath2rest());
        try {
            String getResponse = putClient.send(user.getUsername(), user.getPassphrase(), endpoint, body);
            return parseHttpResponse(getResponse);
        } catch (IOException ioe) {
            return parserServices.createServiceOrderStatus(ParserServices.CODE_BUG, ioe.getMessage());
        }
    }

    private ServiceOrderStatus parseHttpResponse(String httpResponse) throws IOException {
        String code;
        String message;
        if (jsonServices.isJson(httpResponse)) { // json
            RestResponse restResponse = jsonServices.jsonToObject(httpResponse, RestResponse.class);
            if (restResponse.getError() != null) {
                code = restResponse.getError().getCode();
                message = restResponse.getError().getMessage();
            } else {
                code = ParserServices.CODE_OK;
                message = ParserServices.MESSAGE_SUCCESS;
            }
        } else { // html code
            Optional<String> codeError = parserServices.getHtmlCodeContent(httpResponse);
            Optional<String> htmlError = parserServices.getHtmlBodyContent(httpResponse);
            if (codeError.isPresent() && htmlError.isPresent()) {
                code = codeError.get();
                message = htmlError.get();
            } else if (htmlError.isPresent()) {
                code = ParserServices.CODE_BAD;
                message = htmlError.get();
            } else {
                code = ParserServices.CODE_BUG;
                message = httpResponse;
            }
        }
        return parserServices.createServiceOrderStatus(code, message);
    }

}
