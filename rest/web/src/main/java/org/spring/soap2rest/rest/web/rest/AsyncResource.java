package org.spring.soap2rest.rest.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.api.model.AsyncRestRequest;
import org.spring.soap2rest.rest.impl.ResponseGeneratorServices;
import org.spring.soap2rest.rest.impl.ValidationServices;
import org.spring.soap2rest.rest.web.RestResources;
import org.spring.soap2rest.rest.web.RestRoles;
import org.spring.soap2rest.rest.web.utils.ClientLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Async Realm with PreAuthorize security.
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

    private DeferredResult<ResponseEntity> asyncExecute(Supplier<ResponseEntity> supplier) {
        final DeferredResult<ResponseEntity> deffResult = new DeferredResult(TIMEOUT, ResponseGeneratorServices.TIMEOUT_MESSAGE);

        deffResult.onTimeout(() -> {
            deffResult.setErrorResult(
                    ResponseEntity
                            .status(HttpStatus.REQUEST_TIMEOUT)
                            .body(ResponseGeneratorServices.TIMEOUT_MESSAGE)
            );
        });
        CompletableFuture
                .supplyAsync(supplier, executor)
                .thenApply(respEntity -> deffResult.setResult(respEntity))
                .exceptionally(throwable ->
                        deffResult.setErrorResult(responseGeneratorServices.getErrorResponse("500", throwable.getMessage()))
                );

        return deffResult;
    }

    // curl localhost:8079/soap2rest/v1/rest/async
    // http://localhost:8079/soap2rest/v1/rest/async
    @RequestMapping("**/**")
    @PreAuthorize(RestRoles.HAS_CLIENT_ROLE)
    public DeferredResult<ResponseEntity> about() {
        return asyncExecute(() -> {
            log.info(String.format("Async GET request was accepted."));

            return ResponseEntity.ok("Async Realm! Client role.\n");
        });
    }

    // curl localhost:8079/soap2rest/v1/rest/async/auth
    // http://localhost:8079/soap2rest/v1/rest/async/auth
    @RequestMapping("**/auth")
    @PreAuthorize(RestRoles.HAS_ADMIN_ROLE)
    public DeferredResult<ResponseEntity> auth() {
        return asyncExecute(() -> {
            log.info(String.format("Async GET request was accepted."));

            return ResponseEntity.ok("Async Realm! Admin role.\n");
        });
    }

    // curl localhost:8079/soap2rest/v1/rest/async/timeout
    // http://localhost:8079/soap2rest/v1/rest/async/timeout
    @RequestMapping("**/timeout")
    @PreAuthorize(RestRoles.HAS_ADMIN_ROLE)
    public DeferredResult<ResponseEntity> timeoutSimulation() {
        return asyncExecute(() -> {
            log.info(String.format("Async GET request was accepted."));

            try {
                log.warn(String.format("We are sleeping..."));
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                log.error("just in case we log it " + e.getMessage(), e);
            }

            return ResponseEntity.ok("Timeout simulation.\n");
        });
    }

    /**
     * curl -X GET http://localhost:8079/soap2rest/rest/v1/async/response --user restadmin:restadmin
     */
    // http://localhost:8079/soap2rest/v1/rest/async/response
    @RequestMapping(
            value = "/response",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(RestRoles.REST_ADMIN_ROLE)
    @ClientLogger
    public DeferredResult<ResponseEntity> getResponse(HttpServletRequest request) { // @ClientLogger

        return asyncExecute(() -> {
            log.info(String.format("Async GET request was accepted."));

            return ResponseEntity
                    .ok()
                    .body(responseGeneratorServices.getRandomResponse());
        });
    }

    /**
     * curl -X PUT -H "Content-Type: application/json" -d '{
     * "messageId" : "test11",
     * "conversationId" : "test22",
     * "code" : "110",
     * "desc" : "J2ME: Write once - debug everywhere."
     * }' http://localhost:8079/soap2rest/v1/rest/async/notify --user restadmin:restadmin
     */

    // http://localhost:8079/soap2rest/v1/rest/async/notify
    @RequestMapping(value = "/notify", method = RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(RestRoles.HAS_ADMIN_ROLE)
    @ClientLogger
    public DeferredResult<ResponseEntity> notify(
            HttpServletRequest request, // ClientLogger
            @RequestBody AsyncRestRequest asyncRestRequest) throws Exception {

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
