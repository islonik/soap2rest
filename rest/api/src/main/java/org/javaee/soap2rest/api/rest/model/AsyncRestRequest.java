package org.javaee.soap2rest.api.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by nikilipa on 8/26/16.
 */
public class AsyncRestRequest {

    @NotNull
    @JsonProperty
    private String messageId;
    @NotNull
    @JsonProperty
    private String conversationId;
    @NotNull
    @JsonProperty
    private String code;
    @NotNull
    @JsonProperty
    private String desc;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return String.format(
                "AsyncRestRequest object where messageId = '%s', conversationId = '%s', code = '%s', desc = '%s';",
                messageId,
                conversationId,
                code,
                desc
        );
    }
}
