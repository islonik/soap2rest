package org.spring.soap2rest.rest.web.utils;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nikilipa on 3/17/17.
 */
@Aspect
@Component
public class LoggerAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggerAspect.class);

    @Before("@annotation(org.spring.soap2rest.rest.web.utils.ClientLogger) && args(httpRequest, ..)")
    public void client(HttpServletRequest httpRequest)
    {
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
    }

}
