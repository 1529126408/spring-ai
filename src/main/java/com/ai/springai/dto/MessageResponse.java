package com.ai.springai.dto;

import com.ai.springai.model.Message;

/**
 * 消息响应DTO，用于返回AI的回复消息和对话信息
 */
public class MessageResponse {
    private String conversationId; // 对话ID
    private Message message; // AI回复的消息
    private String conversationTitle; // 对话标题

    public MessageResponse() {
    }

    public MessageResponse(String conversationId, Message message, String conversationTitle) {
        this.conversationId = conversationId;
        this.message = message;
        this.conversationTitle = conversationTitle;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }
}