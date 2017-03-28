package org.spring.soap2rest.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

/**
 * Created by nikilipa on 1/31/17.
 */
@SpringBootApplication
@EntityScan("org.spring.soap2rest.rest") // database entities
@ComponentScan({"org.spring.soap2rest.utils", "org.spring.soap2rest.rest"})
@EnableAutoConfiguration
@EnableAspectJAutoProxy
public class Application extends AsyncConfigurerSupport {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        log.warn("Rest Application has started.");

        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);
    }

}
