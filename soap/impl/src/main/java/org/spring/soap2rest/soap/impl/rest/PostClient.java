package org.spring.soap2rest.soap.impl.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostClient implements RestClient {

    private final Logger log = LoggerFactory.getLogger(PostClient.class);

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
        String postResponse = null;
        try {
            log.info(String.format("POST request '%s' to S2R.rest:%n%s%n%s", messageId, endpoint, object));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(object, headers);
            postResponse = restTemplate.postForObject(endpoint, httpEntity, String.class);
        } catch (HttpClientErrorException rce) {
            postResponse = error(rce);
        } finally {
            log.info(String.format("POST response '%s' from S2R.rest:%n%s", messageId, postResponse));
            return postResponse;
        }
    }

}
