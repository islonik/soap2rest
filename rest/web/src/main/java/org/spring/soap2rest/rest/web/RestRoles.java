package org.spring.soap2rest.rest.web;

public interface RestRoles {

    String S2R_REALM_NAME = "S2R_REST_REALM";

    // sync <-> jsr-250
    String REST_ADMIN_ROLE = "WEBSERVICE.SOAP2REST.REST.ADMIN";
    String REST_CLIENT_ROLE = "WEBSERVICE.SOAP2REST.REST.CLIENT";

    // async <-> PreAuthorize
    String HAS_ADMIN_ROLE = "hasRole('WEBSERVICE.SOAP2REST.REST.ADMIN')";
    String HAS_CLIENT_ROLE = "hasRole('WEBSERVICE.SOAP2REST.REST.CLIENT')";

}