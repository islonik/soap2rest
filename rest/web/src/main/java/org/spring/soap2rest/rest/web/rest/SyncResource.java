package org.spring.soap2rest.rest.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.impl.ResponseGeneratorServices;
import org.spring.soap2rest.rest.web.RestResources;
import org.spring.soap2rest.rest.web.RestRoles;
import org.spring.soap2rest.rest.web.utils.ClientLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Sync Realm with jsr-250 security.
 */
@RestController
@RequestMapping(value = RestResources.SYNC_PATH)
public class SyncResource {

    private static final Logger log = LoggerFactory.getLogger(SyncResource.class);

    @Autowired
    private ResponseGeneratorServices responseGeneratorServices;

    // curl localhost:8079/soap2rest/v1/rest/sync/
    // http://localhost:8079/soap2rest/v1/rest/sync/
    @RequestMapping()
    @RolesAllowed(RestRoles.REST_CLIENT_ROLE)
    public String about() {
        return "Sync Realm! Client role. \n";
    }

    // curl localhost:8079/soap2rest/v1/rest/sync/auth
    // http://localhost:8079/soap2rest/v1/rest/sync/auth
    @RequestMapping("**/auth")
    @RolesAllowed(RestRoles.REST_ADMIN_ROLE)
    public String auth() {
        return "Sync Realm! Admin role.\n";
    }

    /**

     curl -X GET http://localhost:8079/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */
    // http://localhost:8079/soap2rest/v1/rest/sync/response
    @RequestMapping(
            value = "/response",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(RestRoles.REST_ADMIN_ROLE)
    @ClientLogger
    public ResponseEntity getResponse(HttpServletRequest request) { // @ClientLogger
        log.info(String.format("Sync GET request was accepted."));

        return ResponseEntity
                .ok()
                .body(responseGeneratorServices.getRandomResponse());
    }

    /**

     curl -X PUT http://localhost:8079/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */
    // http://localhost:8079/soap2rest/v1/rest/sync/response
    @RequestMapping(
            value = "/response",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(RestRoles.REST_ADMIN_ROLE)
    @ClientLogger
    public ResponseEntity putResponse(
            HttpServletRequest request, // @ClientLogger
            @RequestBody Map<String, String> object) {
        log.info(String.format("Sync PUT request was accepted. Map content = %s", object.toString()));

        return ResponseEntity
                .ok()
                .body(responseGeneratorServices.getRandomResponse());
    }

    /**

     curl -X POST -H "Content-Type: application/json" -d '{
     "test44" : "test55"
     }' http://localhost:8079/soap2rest/rest/v1/sync/response --user restadmin:restadmin

     */
    // http://localhost:8079/soap2rest/v1/rest/sync/response
    @RequestMapping(
            value = "/response",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @RolesAllowed(RestRoles.REST_ADMIN_ROLE)
    @ClientLogger
    public ResponseEntity postResponse(
            HttpServletRequest httpRequest, // @ClientLogger
            @RequestBody Map<String, String> object) {
        log.info(String.format("Sync POST request was accepted. Map content = %s", object.toString()));

        return ResponseEntity
                .ok()
                .body(responseGeneratorServices.getRandomResponse());
    }

}
