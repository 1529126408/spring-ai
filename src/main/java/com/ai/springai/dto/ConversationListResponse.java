package com.ai.springai.dto;

import com.ai.springai.model.Conversation;
import java.util.List;

/**
 * 对话列表响应DTO，用于返回所有对话的列表信息
 */
public class ConversationListResponse {
    private List<Conversation> conversations;

    public ConversationListResponse() {
    }

    public ConversationListResponse(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}