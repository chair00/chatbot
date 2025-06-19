package com.project.chatbot;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class PromptLoader {

    public static String loadPrompt(String fileName, String situation) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + fileName);
            String promptTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return promptTemplate.replace("${situation}", situation);

        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 파일 로딩 실패: " + fileName, e);
        }
    }
}
