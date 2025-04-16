package com.ai.springai.service;

import com.ai.springai.model.Conversation;
import com.ai.springai.model.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对话服务类，用于管理对话状态和处理对话逻辑
 */
@Service
public class ConversationService {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    // 使用内存存储对话，实际应用中应该使用数据库
    private final Map<String, Conversation> conversations = new HashMap<>();

    /**
     * 创建新对话
     * @param title 对话标题，如果为空则使用默认标题
     * @return 新创建的对话
     */
    public Conversation createConversation(@RequestParam(value = "title", defaultValue = "新对话") String title) {
        Conversation conversation = new Conversation(title != null && !title.isEmpty() ? title : "新对话");
        conversations.put(conversation.getId(), conversation);
        return conversation;
    }

    /**
     * 获取对话列表
     * @return 所有对话的列表
     */
    public List<Conversation> getConversations() {
        return new ArrayList<>(conversations.values());
    }

    /**
     * 根据ID获取对话
     * @param conversationId 对话ID
     * @return 对话对象，如果不存在则返回null
     */
    public Conversation getConversation(String conversationId) {
        return conversations.get(conversationId);
    }

    /**
     * 向对话添加用户消息并生成AI回复
     * @param conversationId 对话ID
     * @param content 用户消息内容
     * @return AI回复的消息
     */
    public Message addMessageAndGetReply(String conversationId, String content) {
        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            return null;
        }

        // 添加用户消息
        Message userMessage = new Message("user", content);
        conversation.addMessage(userMessage);

        // 生成AI回复
        String aiReply = String.valueOf(generateAiReply(conversation));
        Message aiMessage = new Message("assistant", aiReply);
        conversation.addMessage(aiMessage);

        return aiMessage;
    }

    /**
     * 生成AI回复
     *
     * @param conversation 对话对象
     * @return AI回复内容
     */
    private ChatResponse generateAiReply(Conversation conversation) {
        // 构建上下文，包含对话历史
        StringBuilder contextBuilder = new StringBuilder();
        for (Message message : conversation.getMessages()) {
            if ("user".equals(message.getRole())) {
                contextBuilder.append("用户: ").append(message.getContent()).append("\n");
            } else if ("assistant".equals(message.getRole())) {
                contextBuilder.append("AI: ").append(message.getContent()).append("\n");
            }
        }

        // 获取最后一条用户消息
        String lastUserMessage = conversation.getMessages().stream()
                .filter(m -> "user".equals(m.getRole()))
                .reduce((first, second) -> second)
                .map(Message::getContent)
                .orElse("");

        // 构建提示，包含上下文和最后一条用户消息
        String prompt = "以下是与用户的对话历史:\n" + contextBuilder.toString() + 
                "请根据对话历史回答用户的最后一个问题: " + lastUserMessage;

        // 调用OpenAI API获取回复
        return openAiChatModel.call(new Prompt(new UserMessage(prompt)));
    }

    /**
     * 删除对话
     * @param conversationId 对话ID
     * @return 是否删除成功
     */
    public boolean deleteConversation(String conversationId) {
        return conversations.remove(conversationId) != null;
    }
}