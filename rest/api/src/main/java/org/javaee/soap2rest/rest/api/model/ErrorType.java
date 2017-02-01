package org.javaee.soap2rest.rest.api.model;

/**
 * Created by nikilipa on 8/13/16.
 */
public class ErrorType {

    public String code;
    public String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
