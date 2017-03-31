package org.spring.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GetClient implements RestClient {

    private final Logger log = LoggerFactory.getLogger(GetClient.class);

    private static final AtomicLong getCounter = new AtomicLong();

    private static String getMessageId() {
        return String.format("id-s2r-soap-get-%s", getCounter.incrementAndGet());
    }

    public String send(String user, String pass, String endpoint) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri(endpoint)
                .basicAuthorization(user, pass)
                .build();

        String messageId = getMessageId();
        String getResponse = null;
        try {
            log.info(String.format("GET request '%s' to S2R.rest:%n%s", messageId, endpoint));
            getResponse = restTemplate.getForObject(endpoint, String.class);
        } catch (HttpClientErrorException rce) {
            getResponse = error(rce);
        } finally {
            log.info(String.format("GET response '%s' from S2R.rest:%n%s", messageId, getResponse));
            return getResponse;
        }
    }

}
