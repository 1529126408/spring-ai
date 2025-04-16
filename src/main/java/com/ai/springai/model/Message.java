package com.ai.springai.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 消息模型类，用于存储对话中的单条消息
 */
@Setter
@Getter
public class Message {
    private String id;
    private String role; // 消息角色：user 或 assistant
    private String content; // 消息内容
    private Date timestamp; // 消息时间戳

    public Message() {
        this.id = java.util.UUID.randomUUID().toString();
        this.timestamp = new Date();
    }

    public Message(String role, String content) {
        this();
        this.role = role;
        this.content = content;
    }

}