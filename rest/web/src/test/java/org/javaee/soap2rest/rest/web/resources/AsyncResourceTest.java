package org.javaee.soap2rest.rest.web.resources;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
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
    public void testAboutCase01() throws Exception {
        String response1 = target("async/about").request().get(String.class);
        Assert.assertEquals("Async Realm v1\n", response1);
    }

    @Test(expected = NotFoundException.class)
    public void testAboutCase02() throws Exception {
        final String noResource = new String(Files.readAllBytes(Paths.get("src/test/resources/no_resource.json")));
        String response1 = target("async/about23").request().get(String.class);
        Assert.assertEquals(noResource, response1);
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
