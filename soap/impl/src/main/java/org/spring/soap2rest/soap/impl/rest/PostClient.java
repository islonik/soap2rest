package org.spring.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostClient {

    private final Logger log = LoggerFactory.getLogger(PutClient.class);

    private static final AtomicLong postCounter = new AtomicLong();

    private static String getMessageId() {
        return String.format("id-s2r-soap-post-%s", postCounter.incrementAndGet());
    }

    public String send(String user, String pass, String endpoint, String object) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(endpoint)
                .basicAuthorization(user, pass)
                .build();

        String messageId = getMessageId();

        log.info(String.format("PUT request '%s' to S2R.rest:%n%s%n%s", messageId, endpoint, object));

        String postResponse = restTemplate.postForObject(endpoint, object, String.class);

        log.info(String.format("PUT response '%s' from S2R.rest:%n%s", messageId, postResponse));

        return postResponse;
    }

}
