package org.javaee.soap2rest.utils.services;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.function.Supplier;

@ApplicationScoped
public class JsonServices {

    private static final ObjectMapper mapper = ((Supplier<ObjectMapper>) () -> {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper;
    }).get();

    public String objectToJson(Object tag) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tag);
    }

    public <T>T jsonToObject(String json, Class<T> className) throws IOException {
        return mapper.readValue(json, className);
    }

    public boolean isJson(String json) {
        try {
            if (json.contains("{") && json.contains("}")) {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(json);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

}