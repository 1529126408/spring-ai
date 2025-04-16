package com.ai.springai.service;

import com.ai.springai.model.Conversation;
import com.ai.springai.model.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式对话服务类，用于处理流式AI回复
 */
@Service
public class StreamingConversationService {

    @Autowired
    private OpenAiChatModel openAiChatModel;
    
    @Autowired
    private ConversationService conversationService;

    /**
     * 向对话添加用户消息并获取流式AI回复
     * @param conversationId 对话ID
     * @param content 用户消息内容
     * @return 流式AI回复
     */
    public Flux<ChatResponse> addMessageAndGetStreamingReply(String conversationId, String content) {
        Conversation conversation = conversationService.getConversation(conversationId);
        if (conversation == null) {
            return Flux.empty();
        }

        // 添加用户消息
        Message userMessage = new Message("user", content);
        conversation.addMessage(userMessage);

        // 构建上下文，包含对话历史
        StringBuilder contextBuilder = new StringBuilder();
        for (Message message : conversation.getMessages()) {
            if ("user".equals(message.getRole())) {
                contextBuilder.append("用户: ").append(message.getContent()).append("\n");
            } else if ("assistant".equals(message.getRole())) {
                contextBuilder.append("AI: ").append(message.getContent()).append("\n");
            }
        }

        // 构建提示，包含上下文和最后一条用户消息
        String promptText = "以下是与用户的对话历史:\n" + contextBuilder.toString() + 
                "请根据对话历史回答用户的最后一个问题: " + content;

        // 创建提示对象
        Prompt prompt = new Prompt(new UserMessage(promptText));

        // 创建一个收集完整回复的列表
        List<String> chunks = new ArrayList<>();
        
        // 获取流式回复并收集完整内容
        Flux<ChatResponse> responseFlux = openAiChatModel.stream(prompt)
            .doOnNext(response -> {
                // 收集每个响应片段
                chunks.add(response.getResult().getOutput().getText());
            })
            .doOnComplete(() -> {
                // 流完成后，将完整回复添加到对话历史
                String fullReply = String.join("", chunks);
                Message aiMessage = new Message("assistant", fullReply);
                conversation.addMessage(aiMessage);
            });

        return responseFlux;
    }
}