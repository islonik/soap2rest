package org.spring.soap2rest.soap.impl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.RestResponse;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.spring.soap2rest.soap.impl.generated.ds.ws.DSResponse;
import org.spring.soap2rest.soap.impl.generated.ds.ws.KeyValuesType;
import org.spring.soap2rest.utils.services.JsonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 8/13/16.
 */
@Service
public class SoapOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SoapOrchestrator.class);

    @Autowired
    private ParserServices parserServices;

    @Autowired
    private RestServices restServices;

    @Autowired
    private JsonServices jsonServices;

    public DSResponse syncProcess(DSRequest dsRequest) {
        return process(dsRequest);
    }

    public void asyncProcess(DSRequest dsRequest) {
        try {
            DSResponse dsResponse = process(dsRequest);
            String ackResponse = restServices.sendNotifyRequest(dsResponse);
            log.info("\n" + ackResponse);
        } catch (JsonProcessingException | InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }

    private DSResponse process(DSRequest dsRequest) {
        try {
            List<KeyValuesType> params = dsRequest.getBody().getServiceOrder().getParams();

            Map<String, String> mapBody = new HashMap<>();
            for (KeyValuesType keyValuesType : params) {
                String key = keyValuesType.getKey();
                String value = keyValuesType.getValue();

                mapBody.putIfAbsent(key, value);
            }

            String httpResponse = restServices.getResponseRequest(mapBody);

            // parse http post response
            if (jsonServices.isJson(httpResponse)) { // json

                RestResponse restResponse = parserServices.getRestResponse(httpResponse);
                if (restResponse.getError() != null) {
                    return parserServices.getDSResponse(
                            dsRequest,
                            restResponse.getError().getCode(),
                            restResponse.getError().getMessage()
                    );
                }
            } else { // html code
                Optional<String> htmlError = parserServices.getHtmlBodyContent(httpResponse);
                if (htmlError.isPresent()) {
                    return parserServices.getDSResponse(dsRequest, "400", htmlError.get());
                } else {
                    return parserServices.getDSResponse(dsRequest, "500", httpResponse);
                }
            }

            return parserServices.getDSResponse(dsRequest, "0", "SUCCESS");
        } /*catch (TimeoutException te) {
            return parserServices.getDSResponse(dsRequest, "504", te.getMessage());
        } */catch (Exception e) {
            return parserServices.getDSResponse(dsRequest, "500", e.getMessage());
        }
    }
}