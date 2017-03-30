package org.spring.soap2rest.soap.impl.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by nikilipa on 2/15/17.
 */
public class Service implements Serializable {

    private final String id;
    private final String name;
    private final String type;
    private final String messageId;
    private final String conversationId;
    private final Map<String, String> params;
    private final ConcurrentMap<String, String> optionalParams = new ConcurrentHashMap<>();

    public Service(String id, String name, String type, String messageId, String conversationId, Map<String, String> params) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.params = Collections.unmodifiableMap(params);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getOptional(String key) {
        return optionalParams.get(key);
    }

    public void putOptional(String key, String value) {
        this.optionalParams.put(key, value);
    }

    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", messageId='" + messageId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }
}
