package org.spring.soap2rest.soap.impl.services;

import org.spring.soap2rest.rest.api.model.RestResponse;
import org.spring.soap2rest.soap.impl.generated.ds.ws.*;
import org.spring.soap2rest.utils.services.JsonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Created by nikilipa on 8/13/16.
 */
@Service
public class ParserServices {

    @Autowired
    private JsonServices jsonServices;

    public boolean isAsync(DSRequest dsRequest) {
        return Boolean.valueOf(dsRequest.getBody().getAsyncronousResponse());
    }

    public void setUpDsRequest(DSRequest dsRequest) {
        if (dsRequest.getHeader() != null) {
            Optional<String> conversationId = Optional.ofNullable(dsRequest.getHeader().getConversationId());
            if (!conversationId.isPresent()) {
                dsRequest.getHeader().setConversationId(getConversationId());
            }
            Optional<String> messageId = Optional.ofNullable(dsRequest.getHeader().getMessageId());
            if (!messageId.isPresent()) {
                dsRequest.getHeader().setMessageId(getMessageId());
            }
        } else {
            MessageHeaderType messageHeaderType = new MessageHeaderType();
            messageHeaderType.setMessageId(getMessageId());
            messageHeaderType.setConversationId(getConversationId());
            dsRequest.setHeader(messageHeaderType);
        }
    }

    // S2R-CUUID-2016-7-21T12:18:30.803+02:00
    public String getConversationId() {
        return String.format("S2R-CUUID-%s", getTimeTemplate());
    }

    // S2R-MUUID-2016-7-21T12:18:30.803+02:00
    public String getMessageId() {
        return String.format("S2R-MUUID-%s", getTimeTemplate());
    }

    private String getTimeTemplate() {
        String timestamp = ZonedDateTime.now().toString();
        return timestamp.substring(0, timestamp.indexOf("["));
    }

    // should be Zulu time
    // 2016-07-25T05:28:38.218Z
    public String getTimestamp() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        return now.toString();
    }

    public DSResponse getAckDSResponse(DSRequest dsRequest) {
        return getDSResponse(dsRequest, "0", "Acknowledgement");
    }

    public DSResponse getDSResponse(DSRequest dsRequest, String code, String message) {
        DSResponse dsResponse = new DSResponse();
        dsResponse.setHeader(dsRequest.getHeader());

        DSResponse.Body body = new DSResponse.Body();
        dsResponse.setBody(body);

        ServiceOrderStatus sos = new ServiceOrderStatus();
        body.setServiceOrderStatus(sos);

        Optional<String> orderId = Optional.ofNullable(dsRequest.getBody().getServiceOrder().getServiceOrderID());
        if (orderId.isPresent()) {
            sos.setServiceOrderID(orderId.get());
        }
        StatusType statusType = new StatusType();
        sos.setStatusType(statusType);
        statusType.setCode(code);
        statusType.setDesc(message);

        return dsResponse;
    }

    public Optional<String> getHtmlBodyContent(String htmlResponse) {
        if (htmlResponse.contains("body")) {
            int index = htmlResponse.indexOf("body");
            return Optional.ofNullable(htmlResponse.substring(index + 5, htmlResponse.indexOf("</", index + 5)));
        }
        return Optional.empty();
    }

    public RestResponse getRestResponse(String httpResponse) throws IOException {
        return (RestResponse) jsonServices.jsonToObject(httpResponse, RestResponse.class);
    }

}
