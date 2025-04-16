package com.ai.springai.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 对话模型类，用于存储对话信息和消息历史
 */
@Setter
@Getter
public class Conversation {
    private String id;
    private String title;
    private List<Message> messages;
    private Date createdAt;
    private boolean active;

    public Conversation() {
        this.id = UUID.randomUUID().toString();
        this.title = "新对话";
        this.messages = new ArrayList<>();
        this.createdAt = new Date();
        this.active = false;
    }

    public Conversation(String title) {
        this();
        this.title = title;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

}