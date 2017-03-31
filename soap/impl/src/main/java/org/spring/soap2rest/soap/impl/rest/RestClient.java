package org.spring.soap2rest.soap.impl.rest;

import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by nikilipa on 3/31/17.
 */
public interface RestClient {

    default String error(HttpClientErrorException rce) {
        return String.format(String.format(
                "<code>%s</code><body>%s</body>",
                rce.getStatusCode(),
                rce.getStatusCode().getReasonPhrase()
        ));
    }
}
