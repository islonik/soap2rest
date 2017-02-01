package org.javaee.soap2rest.impl.soap.rest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nikilipa on 9/22/16.
 */
public class AuthenticatorTest {

    @Test
    public void testGetBasicAuthenticationCase01() {
        Authenticator authenticator = new Authenticator("mobsoap", "mobsoap");

        Assert.assertEquals("Basic bW9ic29hcDptb2Jzb2Fw", authenticator.getBasicAuthentication());
    }

    @Test
    public void testGetBasicAuthenticationCase02() {
        Authenticator authenticator = new Authenticator("networkquery", "networkquery");

        Assert.assertEquals("Basic bmV0d29ya3F1ZXJ5Om5ldHdvcmtxdWVyeQ==", authenticator.getBasicAuthentication());
    }
}
