package org.javaee.soap2rest.soap.impl.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.javaee.soap2rest.soap.impl.WildFlyResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by nikilipa on 2/15/17.
 */
@Startup
@Singleton
public class CamelStarter {

    private final Logger log = LoggerFactory.getLogger(CamelStarter.class);

    private final DefaultCamelContext camelContext;
    private final ProducerTemplate template;
    // Apache Camel can process maximum 10 requests in parallel by default.
    // So we can increase this number from 10 to 'core-threads' in ExecutorService.
    // Apache Camel has maxPoolSize = 20 by default.
    // So we can increase this number from 20 to 'max-threads' in ExecutorService.
    private final WildFlyResources wildFlyResources;

    @Inject
    public CamelStarter(CamelContext camelContext, ProducerTemplate template, WildFlyResources wildFlyResources) {
        this.camelContext = (DefaultCamelContext) camelContext;
        this.template = template;
        this.wildFlyResources = wildFlyResources;
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public ProducerTemplate getTemplate() {
        return template;
    }

}