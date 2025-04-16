package com.ai.springai.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class QwqController {

    private final OpenAiChatModel chatModel;

    @Autowired
    public QwqController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }


    @GetMapping(value = "/qwq-stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> qwqStream(String prompt) {
        Prompt message = new Prompt(new UserMessage(prompt));
        return this.chatModel.stream(message);
    }
}
