package org.spring.soap2rest.rest.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by nikilipa on 1/31/17.
 */
@SpringBootApplication
@ComponentScan({"org.spring.soap2rest.utils","org.spring.soap2rest.rest"})
@EnableAsync
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) throws Exception {
        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);
    }

}
