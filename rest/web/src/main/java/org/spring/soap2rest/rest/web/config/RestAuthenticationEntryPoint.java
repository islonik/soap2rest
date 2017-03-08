package org.spring.soap2rest.rest.web.config;

import org.spring.soap2rest.rest.web.RestRoles;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * In case the Authentication fails [invalid/missing credentials], this entry point will get triggered.
 * It is very important, because we don’t want [Spring Security default behavior] of redirecting to a login page on authentication failure [ We don't have a login page].
 */
public class RestAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException, ServletException {
        // Authentication failed, send error response.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("HTTP Status 401 : " + authException.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName(RestRoles.S2R_REALM_NAME);
        super.afterPropertiesSet();
    }


}
