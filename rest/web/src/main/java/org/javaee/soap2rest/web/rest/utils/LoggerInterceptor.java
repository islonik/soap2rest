/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package org.javaee.soap2rest.web.rest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.Serializable;

public class LoggerInterceptor implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);
    
    private static final long serialVersionUID = -2230122751970854554L;

    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext)
            throws Exception {
        Object[] parameters = invocationContext.getParameters();
        HttpServletRequest httpRequest = (HttpServletRequest) parameters[0];

        log.info(
                String.format(
                        "%nWe accepted %s-request where url = %s, Client Address = %s, Client Host = %s, Client Port = %s, User = %s",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURL().toString(),
                        httpRequest.getRemoteAddr(),
                        httpRequest.getRemoteHost(),
                        httpRequest.getRemotePort(),
                        httpRequest.getRemoteUser()
                )
        );

        // before a method invocation
        Object proceedObject = invocationContext.proceed();
        if (proceedObject == null) { // it means we work in Async mode
            return invocationContext;
        }
        // after a method invocation
        Response response = (Response) proceedObject;

        log.info(String.format(
                "Response to a requester:%n%s", response.getEntity()
        ));

        return proceedObject;
    }
}
