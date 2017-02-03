package org.spring.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created by nikilipa on 8/13/16.
 */
@Service
public class PutClient {

    private final Logger log = LoggerFactory.getLogger(PutClient.class);

    private static final RestTemplate restTemplate = new RestTemplate(); // RestTemplate is thread-safe

    public String send(String url, AsyncRestRequest asyncRestRequest) {
        RequestEntity re = RequestEntity.put(URI.create(url)).body(asyncRestRequest);

        log.info(String.format("PUT request to S2R.rest:%n%s%n%s", url, asyncRestRequest));

        ResponseEntity<String> response = restTemplate.exchange(re, String.class);

        log.info(String.format("PUT response from S2R.rest:%n%s", response.getBody()));

        return response.getBody();
    }
}
