package org.spring.soap2rest.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.soap2rest.rest.impl.ResponseGeneratorServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by nikilipa on 1/31/17.
 */
@RestController
@RequestMapping(value = RestResources.SYNC_PATH)
public class SyncResource {

    private static final Logger log = LoggerFactory.getLogger(SyncResource.class);

    @Autowired
    private ResponseGeneratorServices responseGeneratorServices;

    // curl localhost:8079/soap2rest/v1/rest/sync/
    @RequestMapping("**/**")
    public String about() {
        return "Sync Realm!\n";
    }

    // curl localhost:8079/soap2rest/v1/rest/sync/auth
    @RequestMapping("**/auth")
    //@Secured(RestRoles.REST_ROLE)
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public String auth() {
        return "Auth Sync Realm!\n";
    }

    /**
     * curl -X POST -H "Content-Type: application/json" -d '{
     * "test44" : "test55"
     * }' http://localhost:8079/soap2rest/v1/rest/sync/response --user restadmin:restadmin
     */

    // http://localhost:8079/soap2rest/v1/rest/sync/response
    @RequestMapping(value = "/response", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    // TODO: security?
    public ResponseEntity response(
            HttpServletRequest request,
            @RequestBody Map<String, String> object) {

        log.warn(String.format("Sync request was accepted. Map content = %s", object.toString()));
        return ResponseEntity.ok().body(responseGeneratorServices.getRandomResponse());
    }

}
