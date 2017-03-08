package org.spring.soap2rest.soap.impl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.spring.soap2rest.soap.impl.rest.PutClient;
import org.spring.soap2rest.utils.services.JsonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 8/13/16.
 */
@Service
public class RestServices {

    @Autowired
    private JsonServices jsonServices;

    @Autowired
    private DbAuthServices dbAuthServices;

    @Autowired
    private PutClient putClient;

    private static final String path2rest = "http://localhost:8079/soap2rest/v1/rest";

    public String getResponseRequest(Map<String, String> map) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(path2rest)
                .basicAuthorization("restadmin", "restadmin")
                .build();

        String uri = String.format("%s/sync/response", path2rest);
        String postResponse = restTemplate.postForObject(uri, map, String.class);

        return postResponse;
    }

    public String sendNotifyRequest(DSResponse dsResponse)
            throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

        String uri = String.format("%s/async/notify", path2rest);

        AsyncRestRequest asyncRestRequest = new AsyncRestRequest();
        asyncRestRequest.setMessageId(dsResponse.getHeader().getMessageId());
        asyncRestRequest.setConversationId(dsResponse.getHeader().getConversationId());
        asyncRestRequest.setCode(dsResponse.getBody().getServiceOrderStatus().getStatusType().getCode());
        asyncRestRequest.setDesc(dsResponse.getBody().getServiceOrderStatus().getStatusType().getDesc());

        String putResponse = putClient.send(uri, asyncRestRequest);

        return putResponse;
    }
}
