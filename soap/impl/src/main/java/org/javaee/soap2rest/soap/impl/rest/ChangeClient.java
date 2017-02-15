package org.javaee.soap2rest.soap.impl.rest;

import javax.ws.rs.client.Entity;

public interface ChangeClient {

    String send(String user, String pass, String endpoint, Entity body);

}