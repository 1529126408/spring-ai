package com.ai.springai.controller;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class OLLamaController {

    @Autowired
    OllamaChatModel ollamaChatModel;

    @GetMapping("/ollamaR1")
    public String ollama(String prompt) {
        return ollamaChatModel.call(prompt);
    }

    @GetMapping("/ollamaR1-stream")
    public Flux<String> ollamaStream(String prompt) {
        return ollamaChatModel.stream(prompt);
    }
}
