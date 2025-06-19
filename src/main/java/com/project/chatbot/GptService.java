package com.project.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatbot.dto.GptResponse;
import com.project.chatbot.dto.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GptService {

    private final RestTemplate restTemplate;
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    public GptService() {
        this.restTemplate = new RestTemplate();
    }

    public String callGpt(List<Message> history, String situation) throws JsonProcessingException {
        List<Message> messages = new ArrayList<>();

        String systemPrompt = PromptLoader.loadPrompt("system_prompt.txt", situation);
        messages.add(new Message("system", systemPrompt));
        messages.addAll(history);

        // 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4-turbo",
                "messages", messages,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ObjectMapper objectMapper = new ObjectMapper();
        log.info("\n GPT 요청 \n {} \n", objectMapper.writeValueAsString(body));

        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                request,
                String.class
        );


        String fullText = "";
        GptResponse gptResponse = objectMapper.readValue(response.getBody(), GptResponse.class);
        fullText = gptResponse.getChoices().get(0).getMessage().content();
        log.info("\n GPT 응답 content 추출 \n{}\n ", fullText);

        return fullText;
    }
}
