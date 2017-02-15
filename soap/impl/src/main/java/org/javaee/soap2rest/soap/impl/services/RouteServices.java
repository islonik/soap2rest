package org.javaee.soap2rest.soap.impl.services;

/**
 * Created by nikilipa on 2/15/17.
 */
public class RouteServices {

    public static final String ASYNC = "Async";
    public static final String MULTICAST = "Multicast";
    public static final String SYNC = "Sync";


    public static String valueOf(String name) {
        return String.format("direct:%s", name);
    }

}
