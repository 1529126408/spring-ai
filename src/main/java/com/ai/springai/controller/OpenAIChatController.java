package com.ai.springai.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class OpenAIChatController {

    @Autowired
    OpenAiChatModel openAiChatModel;

    @GetMapping("/openAI")
    public String chat(String prompt) {
        return openAiChatModel.call(prompt);
    }

    @GetMapping("/openAI-stream")
    public Flux<String> chatStream(String prompt) {
        return openAiChatModel.stream(prompt);
    }
}
