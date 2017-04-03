package org.javaee.soap2rest.rest.web.resources;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;

/**
 * Created by nikilipa on 4/3/17.
 */
public class SyncResourceTest extends RestTest {

    private static final Logger log = LoggerFactory.getLogger(AsyncResourceTest.class);

    @Override
    protected Application configure() {
        return super.configure();
    }

    @Test
    public void testAbout() throws Exception {
        String response1 = target("sync/about").request().get(String.class);
        Assert.assertEquals("Sync Realm v1\n", response1);
    }

    @Test
    public void testResponse() throws Exception {
        String response1 = target("sync/response").request().get(String.class);
        Assert.assertNotNull(response1);
    }
}
