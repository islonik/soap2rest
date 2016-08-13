package org.javaee.soap2rest.impl.soap.services;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nikilipa on 8/13/16.
 */
public class ParserServiceTest {

    @Test
    public void testConversationId() {
        ParserService parserService = new ParserService();

        String conversationId = parserService.getConversationId();
        Assert.assertTrue(conversationId.startsWith("S2R-CUUID-"));
    }

    @Test
    public void testMessageId() {
        ParserService parserService = new ParserService();

        String messageId = parserService.getMessageId();
        Assert.assertTrue(messageId.startsWith("S2R-MUUID-"));
    }
}
