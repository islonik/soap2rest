package org.javaee.soap2rest.rest.web.resources;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by nikilipa on 3/24/17.
 */
public class AsyncResourceTest extends RestTest {

    private static final Logger log = LoggerFactory.getLogger(AsyncResourceTest.class);

    @Override
    protected Application configure() {
        return super.configure();
    }

    @Test
    public void testNQPResourceOkCase01() throws Exception {
        log.warn("--- Timeout Start");
        final String testTimeout = new String(Files.readAllBytes(Paths.get("src/test/resources/timeout.json")));
        String response1 = target("async/timeout/1").request().get(String.class);
        Assert.assertEquals(testTimeout, response1);
        String response2 = target("async/timeout/2").request().get(String.class);
        Assert.assertEquals(testTimeout, response2);
        String response3 = target("async/timeout/3").request().get(String.class);
        Assert.assertEquals(testTimeout, response3);
    }

}
