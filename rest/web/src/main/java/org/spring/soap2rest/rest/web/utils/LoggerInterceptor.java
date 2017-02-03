package org.spring.soap2rest.rest.web.utils;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nikilipa on 2/1/17.
 */
public class LoggerInterceptor extends HandlerInterceptorAdapter {

    //before the actual handler will be executed
    public boolean preHandle(HttpServletRequest request)
            throws Exception {

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        return true;
    }


}
