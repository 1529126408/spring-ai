package com.ai.springai.controller;

import com.ai.springai.dto.ConversationListResponse;
import com.ai.springai.dto.MessageRequest;
import com.ai.springai.dto.MessageResponse;
import com.ai.springai.model.Conversation;
import com.ai.springai.model.Message;
import com.ai.springai.service.ConversationService;
import com.ai.springai.service.StreamingConversationService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI对话控制器，提供对话管理和消息处理的API接口
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private ConversationService conversationService;
    
    @Autowired
    private StreamingConversationService streamingConversationService;

    /**
     * 创建新对话
     * @param title 对话标题（可选）
     * @return 新创建的对话信息
     */
    @PostMapping("/conversations")
    public Conversation createConversation(@RequestParam(required = false) String title) {
        return conversationService.createConversation(title);
    }

    /**
     * 获取所有对话列表
     * @return 对话列表
     */
    @GetMapping("/conversations")
    public ConversationListResponse getConversations() {
        List<Conversation> conversations = conversationService.getConversations();
        return new ConversationListResponse(conversations);
    }

    /**
     * 获取指定对话的详细信息
     * @param conversationId 对话ID
     * @return 对话详细信息
     */
    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversation(@PathVariable String conversationId) {
        return conversationService.getConversation(conversationId);
    }

    /**
     * 删除指定对话
     * @param conversationId 对话ID
     * @return 操作结果
     */
    @DeleteMapping("/conversations/{conversationId}")
    public boolean deleteConversation(@PathVariable String conversationId) {
        return conversationService.deleteConversation(conversationId);
    }

    /**
     * 发送消息并获取AI回复（非流式）
     * @param request 消息请求
     * @return AI回复消息
     */
    @PostMapping("/messages")
    public MessageResponse sendMessage(@RequestBody MessageRequest request) {
        String conversationId = request.getConversationId();
        String content = request.getContent();
        
        // 如果没有指定对话ID，则创建新对话
        if (conversationId == null || conversationId.isEmpty()) {
            Conversation newConversation = conversationService.createConversation(null);
            conversationId = newConversation.getId();
        }
        
        // 添加消息并获取回复
        Message reply = conversationService.addMessageAndGetReply(conversationId, content);
        Conversation conversation = conversationService.getConversation(conversationId);
        
        return new MessageResponse(conversationId, reply, conversation.getTitle());
    }

    /**
     * 发送消息并获取流式AI回复
     * @param request 消息请求
     * @return 流式AI回复
     */
    @PostMapping(value = "/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> sendMessageStream(@RequestBody MessageRequest request) {
        String conversationId = request.getConversationId();
        String content = request.getContent();
        
        // 如果没有指定对话ID，则创建新对话
        if (conversationId == null || conversationId.isEmpty()) {
            Conversation newConversation = conversationService.createConversation(null);
            conversationId = newConversation.getId();
        }
        
        // 添加消息并获取流式回复
        return streamingConversationService.addMessageAndGetStreamingReply(conversationId, content);
    }
}