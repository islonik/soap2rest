package org.spring.soap2rest.rest.web.resources;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Created by nikilipa on 3/27/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AsyncResourceTest {

    @Test
    public void testTimeout() throws Exception {
        Assert.assertTrue("During this test you should be able to see 'This thread was interrupted by a timeout event.' this row", true);
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                .rootUri("http://localhost:8079/soap2rest/v1/rest")
                .basicAuthorization("restadmin", "restadmin")
                .build();

        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

            }
        });

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/async/timeout", String.class);
        Assert.assertEquals(408, getResponse.getStatusCodeValue());
        Assert.assertEquals("Request timeout occurred.", getResponse.getBody());

    }
}
