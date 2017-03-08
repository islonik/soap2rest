package org.spring.soap2rest.rest.web;

/**
 * Created by nikilipa on 2/1/17.
 */
public interface RestResources {

    String BASE_PATH = "/soap2rest/v1/rest";

    String SYNC_PATH = BASE_PATH + "/sync";
    String SYNC_MATCHER = SYNC_PATH + "/**";

    String ASYNC_PATH = BASE_PATH + "/async";
    String ASYNC_MATCHER = ASYNC_PATH + "/**";

}
