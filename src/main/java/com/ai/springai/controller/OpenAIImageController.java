package com.ai.springai.controller;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAIImageController {

    @Autowired
    OpenAiImageModel openAiImageModel;

    @GetMapping(value = "/openAI-image",produces = "text/html")
    public String generateImages(String prompt) {
        ImageResponse response = openAiImageModel.call(new ImagePrompt(prompt));
        Image output = response.getResult().getOutput();

        String url = output.getUrl();
        return "<img src='" + url + "'/>";
    }
}
