package org.spring.soap2rest.soap.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan({"org.spring.soap2rest.utils","org.spring.soap2rest.soap"})
@EnableAsync
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) throws Exception {
        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);
    }

}