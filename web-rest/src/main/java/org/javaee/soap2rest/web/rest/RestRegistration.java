package org.javaee.soap2rest.web.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nikilipa on 7/19/16.
 */
@ApplicationPath("")
public class RestRegistration extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();

        set.add(AsyncEndpointResource.class);
        set.add(NotifyResource.class);

        return set;
    }
}

