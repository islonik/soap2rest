package org.javaee.soap2rest.soap.impl.camel;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.Optional;
import java.util.Set;

/**
 * Created by nikilipa on 2/15/17.
 */
public class S2RAggregationStrategy implements AggregationStrategy {

    private final Set<String> keys;

    public S2RAggregationStrategy(Set<String> keys) {
        this.keys = keys;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        if (newExchange == null) {
            return oldExchange;
        }

        for (String key : keys) {
            oldExchange = handle(oldExchange, newExchange, key);
        }

        return oldExchange;
    }

    private Exchange handle(Exchange returnExchange, Exchange check, String key) {
        Optional<Object> value = Optional.ofNullable(check.getIn().getHeader(key));
        value.ifPresent(o -> returnExchange.getIn().setHeader(key, o));
        return returnExchange;
    }

}
