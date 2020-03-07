package org.javaee.soap2rest.soap.impl.services;

import org.javaee.soap2rest.rest.api.model.RestResponse;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;
import org.javaee.soap2rest.soap.impl.model.sql.AuthUser;
import org.javaee.soap2rest.soap.impl.rest.ChangeClient;
import org.javaee.soap2rest.soap.impl.rest.GetClient;
import org.javaee.soap2rest.soap.impl.rest.PostClient;
import org.javaee.soap2rest.soap.impl.rest.PutClient;
import org.javaee.soap2rest.soap.impl.services.sql.DbAuthServices;
import org.javaee.soap2rest.utils.properties.S2RProperty;
import org.javaee.soap2rest.utils.services.JsonServices;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by nikilipa on 8/13/16.
 */
@ApplicationScoped
public class RestServices {

    @Inject
    @S2RProperty("s2r.rest.host")
    private String restHost;

    @Inject
    private GetClient getClient;

    @Inject
    private PutClient putClient;

    @Inject
    private PostClient postClient;

    @Inject
    private JsonServices jsonServices;

    @Inject
    private ParserServices parserServices;

    @Inject
    private DbAuthServices dbAuthServices;

    public void setRestHost(String restHost) {
        this.restHost = restHost;
    }

    public void setGetClient(GetClient getClient) {
        this.getClient = getClient;
    }

    public void setPutClient(PutClient putClient) {
        this.putClient = putClient;
    }

    public void setPostClient(PostClient postClient) {
        this.postClient = postClient;
    }

    public void setJsonServices(JsonServices jsonServices) {
        this.jsonServices = jsonServices;
    }

    public void setParserServices(ParserServices parserServices) {
        this.parserServices = parserServices;
    }

    public void setDbAuthServices(DbAuthServices dbAuthServices) {
        this.dbAuthServices = dbAuthServices;
    }

    public String path2Rest() {
        return restHost;
    }

    public ServiceOrderStatus sendGetRequest(Service service, String urlTemplate) {
        try {

            AuthUser user = dbAuthServices.getUser(service.getName());

            String url = String.format(urlTemplate, path2Rest());

            String getResponse = getClient.get(user.getUsername(), user.getPassphrase(), url);

            return parseHttpResponse(getResponse);
        } catch (IOException ioe) {
            return parserServices.createServiceOrderStatus(ParserServices.CODE_BUG, ioe.getMessage());
        }
    }

    public ServiceOrderStatus sendPostRequest(Service service, String urlTemplate, String body) {
        // PostClient postClient = CDI.current().select(PostClient.class).get();
        AuthUser user = dbAuthServices.getUser(service.getName());
        return sendChange(postClient, user, urlTemplate, body);
    }

    public ServiceOrderStatus sendPutRequest(Service service, String urlTemplate, String body) {
        // PutClient putClient = CDI.current().select(PutClient.class).get();
        AuthUser user = dbAuthServices.getUser(service.getName());
        return sendChange(putClient, user, urlTemplate, body);
    }

    private ServiceOrderStatus sendChange(ChangeClient client, AuthUser user, String urlTemplate, String body) {
        try {
            String url = String.format(urlTemplate, path2Rest());

            String putResponse = client.send(user.getUsername(), user.getPassphrase(), url, Entity.json(body));

            return parseHttpResponse(putResponse);
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
            Optional<String> htmlError = parserServices.getHtmlBodyContent(httpResponse);
            if (htmlError.isPresent()) {
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
