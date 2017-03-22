package org.spring.soap2rest.soap.impl.services;

import org.spring.soap2rest.soap.impl.generated.ds.ws.*;
import org.spring.soap2rest.soap.impl.model.Service;
import org.spring.soap2rest.soap.impl.model.ServiceType;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ParserServices {

    public static final String CODE_OK = "0";
    public static final String CODE_BAD = "400";
    public static final String CODE_BUG = "500";
    public static final String CODE_TIMEOUT = "504";
    public static final String MESSAGE_ACK = "Acknowledgement";
    public static final String MESSAGE_SUCCESS = "SUCCESS";
    public static final String MESSAGE_ERROR = "Internal Server Error";

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

    public DSResponse getDSResponse(DSRequest dsRequest, ServiceOrderStatus sos) {
        DSResponse dsResponse = new DSResponse();
        dsResponse.setHeader(dsRequest.getHeader());

        DSResponse.Body body = new DSResponse.Body();
        dsResponse.setBody(body);

        sos.setServiceOrderID(dsRequest.getBody().getServiceOrder().getServiceOrderID());
        body.setServiceOrderStatus(sos);

        return dsResponse;
    }

    public Optional<String> getHtmlBodyContent(String htmlResponse) {
        if (htmlResponse.contains("body")) {
            int index = htmlResponse.indexOf("body");
            return Optional.ofNullable(htmlResponse.substring(index + 5, htmlResponse.indexOf("</", index + 5)));
        }
        return Optional.empty();
    }

    public Service xml2service(DSRequest dsRequest) {
        List<KeyValuesType> xmlParams = dsRequest.getBody().getServiceOrder().getParams();

        Map<String, String> serviceParams = new HashMap<>();
        for (KeyValuesType keyValuesType : xmlParams) {
            String key = keyValuesType.getKey();
            String value = keyValuesType.getValue();

            serviceParams.putIfAbsent(key, value);
        }
        return new Service(
                dsRequest.getBody().getServiceOrder().getServiceOrderID(),
                dsRequest.getBody().getServiceOrder().getServiceName(),
                ServiceType.valueOf(dsRequest.getBody().getServiceOrder()),
                dsRequest.getHeader().getMessageId(),
                dsRequest.getHeader().getConversationId(),
                serviceParams
        );
    }

    public ServiceOrderStatus createServiceOrderStatus(String code, String message) {
        ServiceOrderStatus serviceOrderStatus = new ServiceOrderStatus();
        StatusType statusType = new StatusType();
        statusType.setCode(code);
        statusType.setDesc(message);
        serviceOrderStatus.setStatusType(statusType);
        return serviceOrderStatus;
    }

}
