package org.javaee.soap2rest.impl.rest.services;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nikilipa on 2/1/17.
 */
public class ResponseGeneratorServicesTest {

    @Test
    public void testAckResponseCase01() {
        ResponseGeneratorServices responseGeneratorServices = new ResponseGeneratorServices();
        Assert.assertEquals(
                "{\n" +
                        "\t\"status\" : \"Acknowledgement\"\n" +
                        "}",
                responseGeneratorServices.getAckResponse()
        );
    }

    @Test
    public void testSimpleJsonErrorCase01() {
        ResponseGeneratorServices responseGeneratorServices = new ResponseGeneratorServices();
        Assert.assertEquals(
                "{\n" +
                        "  \"error\" : {\n" +
                        "    \"code\" : \"304\",\n" +
                        "    \"message\" : \"Hell O\"\n" +
                        "  }\n" +
                        "}",
                responseGeneratorServices.getSimpleJsonError("304", "Hell O")
        );
    }
}
