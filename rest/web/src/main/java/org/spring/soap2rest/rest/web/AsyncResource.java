package org.spring.soap2rest.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.rest.impl.ResponseGeneratorServices;
import org.spring.soap2rest.rest.impl.ValidationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Created by nikilipa on 2/1/17.
 */
@RestController
@RequestMapping(value = RestResources.ASYNC_PATH)
public class AsyncResource {

    private static final Logger log = LoggerFactory.getLogger(AsyncResource.class);

    private static final long TIMEOUT = 500; // ms

    @Autowired
    private ValidationServices validationServices;

    @Autowired
    private ResponseGeneratorServices responseGeneratorServices;

    private ExecutorService executor = Executors.newCachedThreadPool();

    // curl localhost:8079/soap2rest/v1/rest/async
    @RequestMapping("**/**")
    public String about() {
        return "Async Realm!\n";
    }

    private DeferredResult<ResponseEntity> asyncExecute(Supplier<ResponseEntity> supplier) {
        DeferredResult<ResponseEntity> result = new DeferredResult(TIMEOUT, ResponseGeneratorServices.TIMEOUT_MESSAGE);

        CompletableFuture
                .supplyAsync(supplier, executor)
                .thenApply(respEntity -> result.setResult(respEntity))
                .exceptionally(throwable ->
                        result.setErrorResult(responseGeneratorServices.getErrorResponse("500", throwable.getMessage()))
                );

        return result;
    }

    /**
     *
      curl -X PUT -H "Content-Type: application/json" -d '{
      "messageId" : "test11",
      "conversationId" : "test22",
      "code" : "110",
      "desc" : "J2ME: Write once - debug everywhere."
      }' http://localhost:8079/soap2rest/v1/rest/async/notify --user restadmin:restadmin

     */

    // http://localhost:8079/soap2rest/v1/rest/async/notify
    @RequestMapping(value = "/notify", method = RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    // TODO: security?
    public DeferredResult<ResponseEntity> notify(
            HttpServletRequest request,
            @RequestBody AsyncRestRequest asyncRestRequest) throws Exception {

        // TODO: move to an interceptor?
        log.info(
                String.format(
                        "%nWe accepted %s-request where url = %s, Client Address = %s, Client Host = %s, Client Port = %s, User = %s",
                        request.getMethod(),
                        request.getRequestURL().toString(),
                        request.getRemoteAddr(),
                        request.getRemoteHost(),
                        request.getRemotePort(),
                        request.getRemoteUser()
                )
        );

        return asyncExecute(() -> {
            log.warn(String.format("Async request was accepted. %s", asyncRestRequest.toString()));
            Optional<ResponseEntity> errorResp = validationServices.validAsyncRestRequest(asyncRestRequest);
            if (errorResp.isPresent()) {
                return errorResp.get();
            } else {
                log.info(String.format("Async request was validated. %s", asyncRestRequest.toString()));
                ResponseEntity okResp = ResponseEntity.ok(responseGeneratorServices.getAckResponse());
                return okResp;
            }
        });
    }

}
