package org.spring.soap2rest.rest.impl;

import org.junit.Assert;
import org.junit.Test;
import org.spring.soap2rest.rest.impl.services.ResponseGeneratorServices;

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
