package org.javaee.soap2rest.soap.impl.rest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nikilipa on 9/22/16.
 */
public class AuthenticatorTest {


    @Test
    public void testGetBasicAuthenticationCase01() {
        Authenticator authenticator = new Authenticator("restadmin", "restadmin");

        Assert.assertEquals("Basic cmVzdGFkbWluOnJlc3RhZG1pbg==", authenticator.getBasicAuthentication());
    }

}
