package org.javaee.soap2rest.soap.impl.logic;

import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.javaee.soap2rest.soap.impl.services.RestServices;
import org.javaee.soap2rest.utils.services.JsonServices;

import javax.inject.Inject;

/**
 * Created by nikilipa on 2/15/17.
 */
public abstract class AbstractLogic implements CamelInvoker {

    @Inject
    protected JsonServices jsonServices;

    @Inject
    protected ParserServices parserServices;

    @Inject
    protected RestServices restServices;

    public void setJsonServices(JsonServices jsonServices) {
        this.jsonServices = jsonServices;
    }

    public void setParserServices(ParserServices parserServices) {
        this.parserServices = parserServices;
    }

    public void setRestServices(RestServices restServices) {
        this.restServices = restServices;
    }

}
