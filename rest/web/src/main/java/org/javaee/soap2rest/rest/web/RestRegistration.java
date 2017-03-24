package org.javaee.soap2rest.rest.web;

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

        set.add(AsyncResource.class);
        set.add(SyncResource.class);

        return set;
    }
}

