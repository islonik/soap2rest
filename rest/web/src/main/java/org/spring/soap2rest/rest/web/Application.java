package org.spring.soap2rest.rest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by nikilipa on 1/31/17.
 */
@SpringBootApplication
@ComponentScan({"org.spring.soap2rest.utils","org.spring.soap2rest.rest"})
@EnableAsync
@EnableAutoConfiguration
@EnableAspectJAutoProxy
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        log.warn("Rest Application has started.");

        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);
    }

}
