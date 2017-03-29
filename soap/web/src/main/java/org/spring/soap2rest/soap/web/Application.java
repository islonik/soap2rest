package org.spring.soap2rest.soap.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan({"org.spring.soap2rest.utils","org.spring.soap2rest.soap"})
@EnableAutoConfiguration
@EnableIntegration
@IntegrationComponentScan({"org.spring.soap2rest.soap"})
public class Application {

    public static void main(String[] args) throws Exception {
        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);
    }

}