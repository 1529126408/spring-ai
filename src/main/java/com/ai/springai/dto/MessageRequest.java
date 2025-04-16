package com.ai.springai.dto;

/**
 * 消息请求DTO，用于接收用户发送的消息
 */
public class MessageRequest {
    private String conversationId; // 对话ID，如果为空则创建新对话
    private String content; // 消息内容

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}