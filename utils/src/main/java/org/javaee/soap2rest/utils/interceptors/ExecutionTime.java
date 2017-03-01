package org.javaee.soap2rest.utils.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * Created by nikilipa on 3/1/17.
 */
public class ExecutionTime {

    private static final Logger log = LoggerFactory.getLogger(ExecutionTime.class);

    @AroundInvoke
    public Object performance(InvocationContext invocationContext)
            throws Exception {
        long start = System.currentTimeMillis();

        Method method = invocationContext.getMethod();

        Object object = invocationContext.proceed();

        long end = System.currentTimeMillis();

        log.info("Execution time is {} for the method '{}'", (end - start), method);

        return object;
    }

}
