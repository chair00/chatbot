package com.project.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatbot.dto.GptResponse;
import com.project.chatbot.dto.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GptService {

    private final RestTemplate restTemplate; //GPT API 사용할 때 쓰는건가봐
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions"; // 넣어줘야함

    @Value("${openai.api.key}")
    private String apiKey;

    public GptService() {
        this.restTemplate = new RestTemplate();
    }

    public String callGpt(List<Message> history, String context) throws JsonProcessingException {
        List<Message> messages = new ArrayList<>();

        // system 프롬프트 (context 포함)
        messages.add(new Message("system", """
                    당신은 한국어 문장을 교정하고 상황에 맞게 응답하는 AI입니다.
                    상황: %s
                    형식:
                    교정: [교정된 문장]
                    응답: [자연스러운 응답]
                """.formatted(context)));

        messages.addAll(history); // 기존 히스토리 포함

        // ✅ 로그 찍기
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String gptRequestJson = objectMapper.writeValueAsString(Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", messages,
                    "temperature", 0.7
            ));
            System.out.println("\n======= ✅ GPT 요청 JSON =======");
            System.out.println(gptRequestJson);
            System.out.println("================================\n");
        } catch (JsonProcessingException e) {
            System.out.println("❌ GPT 요청 JSON 직렬화 실패: " + e.getMessage());
        }

        // 요청 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", messages,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                request,
                String.class
        );

        GptResponse gptResponse = objectMapper.readValue(response.getBody(), GptResponse.class);
        String fullText = gptResponse.getChoices().get(0).getMessage().getContent();

        return fullText; // GPT 응답 원문 (JSON)
    }
}
