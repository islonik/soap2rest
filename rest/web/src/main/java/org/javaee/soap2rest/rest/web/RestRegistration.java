package org.javaee.soap2rest.rest.web;

import org.javaee.soap2rest.rest.web.resources.AsyncResource;
import org.javaee.soap2rest.rest.web.resources.SyncResource;
import org.javaee.soap2rest.rest.web.utils.BadURIExceptionMapper;

import javax.ejb.EJBAccessException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nikilipa on 7/19/16.
 */
@ApplicationPath("")
public class RestRegistration extends Application {

    public static final String RESOURCE_NOT_FOUND = "Resource doesn't exist";

    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();

        set.add(AsyncResource.class);
        set.add(SyncResource.class);

        set.add(BadURIExceptionMapper.class);
        set.add(EJBAccessException.class);

        return set;
    }
}

