package org.javaee.soap2rest.impl.soap.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.javaee.soap2rest.api.rest.model.AsyncRestRequest;
import org.javaee.soap2rest.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.impl.soap.model.AuthUser;
import org.javaee.soap2rest.impl.soap.rest.PostClient;
import org.javaee.soap2rest.impl.soap.rest.PutClient;
import org.javaee.soap2rest.utils.services.JsonServices;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 8/13/16.
 */
@ApplicationScoped
public class RestServices {

    @Inject
    private JsonServices jsonServices;

    @Inject
    private DbAuthServices dbAuthServices;

    public String sendNotifyRequest(Map<String, String> map)
            throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

        PostClient postClient = CDI.current().select(PostClient.class).get();

        AuthUser user = dbAuthServices.getUser();

        String url = String.format("%s/sync/response", postClient.getRestHost());

        String postResponse = postClient.send(user.getUser(), user.getPass(), url, Entity.json(jsonServices.objectToJson(map)));

        return postResponse;
    }


    public String sendResponse(DSResponse dsResponse)
            throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

        PutClient putClient = CDI.current().select(PutClient.class).get();

        AuthUser user = dbAuthServices.getUser();

        String url = String.format("%s/async/notify", putClient.getRestHost());

        AsyncRestRequest asyncRestRequest = new AsyncRestRequest();
        asyncRestRequest.setMessageId(dsResponse.getHeader().getMessageId());
        asyncRestRequest.setConversationId(dsResponse.getHeader().getConversationId());
        asyncRestRequest.setCode(dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        asyncRestRequest.setDesc(dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());

        String postResponse = putClient.send(user.getUser(), user.getPass(), url, Entity.json(jsonServices.objectToJson(asyncRestRequest)));

        return postResponse;
    }
}
