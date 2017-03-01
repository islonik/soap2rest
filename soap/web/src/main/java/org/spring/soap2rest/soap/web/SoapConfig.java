package org.spring.soap2rest.soap.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by nikilipa on 2/2/17.
 */
@EnableWs
@Configuration
public class SoapConfig extends WsConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SoapConfig.class);

    private Properties readUsersFile() throws IOException {
        // it works one time during spring-boot startup
        Properties users = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("users.properties")) {
            users.load(inputStream);
        }
        return users;
    }

    @Bean
    public SimplePasswordValidationCallbackHandler securityCallbackHandler() throws IOException {
        SimplePasswordValidationCallbackHandler callbackHandler = new SimplePasswordValidationCallbackHandler();
        callbackHandler.setUsers(readUsersFile());
        return callbackHandler;
    }

    @Bean
    public Wss4jSecurityInterceptor securityInterceptor() throws IOException {
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setValidateRequest(true);
        securityInterceptor.setValidationActions("UsernameToken");
        securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());
        return securityInterceptor;
    }

    @Override
    public void addInterceptors(List interceptors) {
        try {
            interceptors.add(securityInterceptor());
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            throw new RuntimeException(ioe);
        }
    }

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, SoapResources.SOAP_PATH);
    }

}
