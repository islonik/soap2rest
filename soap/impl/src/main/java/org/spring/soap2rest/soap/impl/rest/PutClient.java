package org.spring.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PutClient {

    private final Logger log = LoggerFactory.getLogger(PutClient.class);

    private static final AtomicLong putCounter = new AtomicLong();

    private static String getMessageId() {
        return String.format("id-s2r-soap-put-%s", putCounter.incrementAndGet());
    }

    public String send(String user, String pass, String endpoint, AsyncRestRequest asyncRestRequest) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(endpoint)
                .basicAuthorization(user, pass)
                .build();

        RequestEntity re = RequestEntity.put(URI.create(endpoint)).body(asyncRestRequest);

        String messageId = getMessageId();

        log.info(String.format("PUT request '%s' to S2R.rest:%n%s%n%s", messageId, endpoint, asyncRestRequest));

        ResponseEntity<String> response = restTemplate.exchange(re, String.class);

        log.info(String.format("PUT response '%s' from S2R.rest:%n%s", messageId, response.getBody()));

        return response.getBody();
    }
}
