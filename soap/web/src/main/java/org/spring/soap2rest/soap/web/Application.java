package org.spring.soap2rest.soap.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@ComponentScan({"org.spring.soap2rest.utils", "org.spring.soap2rest.soap"})
@EnableAutoConfiguration(exclude = { // we don't use tables in SOAP part so we should disable all database related auto configuration in Spring Boot.
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableIntegration
@IntegrationComponentScan({"org.spring.soap2rest.soap"})
public class Application {

    public static void main(String[] args) throws Exception {
        Object[] sources = {Application.class};
        SpringApplication.run(sources, args);
    }

}