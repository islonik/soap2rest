package org.javaee.soap2rest.impl.rest.model;

/**
 * Created by nikilipa on 8/13/16.
 */
public class RestResponse {

    private String status;
    private ErrorType error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorType getError() {
        return error;
    }

    public void setError(ErrorType error) {
        this.error = error;
    }

}
