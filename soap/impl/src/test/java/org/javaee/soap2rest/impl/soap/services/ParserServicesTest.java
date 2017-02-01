package org.javaee.soap2rest.impl.soap.services;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nikilipa on 8/13/16.
 */
public class ParserServicesTest {

    @Test
    public void testConversationId() {
        ParserServices parserServices = new ParserServices();

        String conversationId = parserServices.getConversationId();
        Assert.assertTrue(conversationId.startsWith("S2R-CUUID-"));
    }

    @Test
    public void testMessageId() {
        ParserServices parserServices = new ParserServices();

        String messageId = parserServices.getMessageId();
        Assert.assertTrue(messageId.startsWith("S2R-MUUID-"));
    }
}
