package org.javaee.soap2rest.soap.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

/**
 * Created by nikilipa on 2/15/17.
 */
@Startup
@ApplicationScoped
public class CamelStarter {

    private final Logger log = LoggerFactory.getLogger(CamelStarter.class);

    @Inject
    private volatile DefaultCamelContext camelContext;

    private volatile ProducerTemplate template;

    // Apache Camel can process maximum 10 requests in parallel by default.
    // So we can increase this number from 10 to 'core-threads' in ExecutorService.
    // Apache Camel has maxPoolSize = 20 by default.
    // So we can increase this number from 20 to 'max-threads' in ExecutorService.
    @Resource(name = WildFlyResources.SOAP_EXECUTOR)
    private ExecutorService executorService;

    @PostConstruct
    public void setUp() {
        try {
            camelContext.start();

            template = camelContext.createProducerTemplate();
            template.setExecutorService(executorService);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Camel Starter failed to run.");
        }
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public ProducerTemplate getTemplate() {
        return template;
    }

    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = (DefaultCamelContext)camelContext;
    }

    public void setTemplate(ProducerTemplate template) {
        this.template = template;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

}