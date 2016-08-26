package org.javaee.soap2rest.impl.soap.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.javaee.soap2rest.api.rest.model.RestResponse;
import org.javaee.soap2rest.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.impl.generated.ds.ws.DSResponse;
import org.javaee.soap2rest.impl.generated.ds.ws.KeyValuesType;
import org.javaee.soap2rest.utils.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by nikilipa on 8/13/16.
 */
@ApplicationScoped
public class SoapOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SoapOrchestrator.class);

    @Inject
    private ParserService parserService;

    @Inject
    private RestService restService;

    @Inject
    private JsonService jsonService;

    public DSResponse syncProcess(DSRequest dsRequest) {
        return process(dsRequest);
    }

    public void asyncProcess(DSRequest dsRequest) {
        try {
            DSResponse dsResponse = process(dsRequest);
            String ackResponse = restService.sendResponse(dsResponse);
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

            String httpResponse = restService.sendNotifyRequest(mapBody);

            // parse http post response
            if (jsonService.isJson(httpResponse)) { // json

                RestResponse restResponse = parserService.getRestResponse(httpResponse);
                if (restResponse.getError() != null) {
                    return parserService.getDSResponse(
                            dsRequest,
                            restResponse.getError().getCode(),
                            restResponse.getError().getMessage()
                    );
                }
            } else { // html code
                Optional<String> htmlError = parserService.getHtmlBodyContent(httpResponse);
                if (htmlError.isPresent()) {
                    return parserService.getDSResponse(dsRequest, "400", htmlError.get());
                } else {
                    return parserService.getDSResponse(dsRequest, "500", httpResponse);
                }
            }

            return parserService.getDSResponse(dsRequest, "0", "SUCCESS");
        } catch (TimeoutException te) {
            return parserService.getDSResponse(dsRequest, "504", te.getMessage());
        } catch (Exception e) {
            return parserService.getDSResponse(dsRequest, "500", e.getMessage());
        }
    }
}
