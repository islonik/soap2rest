package org.javaee.soap2rest.soap.impl.logic;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.ServiceOrderStatus;
import org.javaee.soap2rest.soap.impl.model.Service;
import org.javaee.soap2rest.soap.impl.services.ParserServices;
import org.javaee.soap2rest.soap.impl.services.RestServices;
import org.javaee.soap2rest.utils.services.JsonServices;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by nikilipa on 2/15/17.
 */
public abstract class AbstractLogic implements FastLogic, MediumLogic, SlowLogic {

    public static final String KEY_PERFORMANCE_EXECUTION = "PerformanceTime:Execution";

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

    public void setUpPerformanceMetrics(Service service, ServiceOrderStatus serviceOrderStatus, String result) {
        Optional.ofNullable(service.getParams().get(KEY_PERFORMANCE_EXECUTION)).ifPresent(value -> {
            boolean isExecutionMetric = Boolean.valueOf(value);

            serviceOrderStatus.getStatusType().setResult(result);
        });
    }

}
