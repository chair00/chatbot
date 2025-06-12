package com.project.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatbot.dto.GptResponse;
import com.project.chatbot.dto.Message;
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

    private final RestTemplate restTemplate; //GPT API 사용할 때 쓰는건가봐
    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key}")
    private String apiKey;

    public GptService() {
        this.restTemplate = new RestTemplate();
    }

    public String callGpt(List<Message> history, String situation) throws JsonProcessingException {
        List<Message> messages = new ArrayList<>();

        // system 프롬프트 (context 포함)
//        messages.add(new Message("system", """
//                    너는 한국어와 베트남어를 유창하게 잘하며 각 나라의 문화에 대해 잘 알고 있다. 그래서 너는 한국어를 배우는 베트남인을 도와주는 친근한 대화 파트너이다.
//                    사용자가 입력한 문장은 베트남어, 한국어, 또는 혼용된 말일 수 있고, 문법 오류나 오타, 어색한 표현, 존댓말/반말 혼용이 있을 수 있다.
//                    특히 입력 문장에 철자나 단어 오타가 있더라도, 사용자의 의도를 대화 상황에 맞게 말의 의미를 유추해서 자연스럽게 고쳐줘.
//                    대화 상황: %s에 맞춰 말투(반말/존댓말)를 조정해 줘. 예를 들어, 친구라면 반말을 쓰고 처음 보는 사람이면 존댓말을 쓰는 거처럼.
//
//                    너의 역할은:
//                    1. 사용자의 말을 자연스럽고 상황에 맞는 어투로 한국어로 고치기.\s
//                    2. 상황에 맞게 너의 역할을 생각해서 반말/존댓말을 자연스럽게 사용하기.
//                    3. 먼저 고쳐준 문장을 출력.
//                    4. 이어서 상황에 맞게 자연스럽게 대화를 이어가는 말하기.
//
//                    ❗ 항상 아래 형식을 반드시 따라야 해:
//                    - 교정: (자연스럽게 고친 문장)
//                    - 응답: (이어지는 자연스러운 대화 멘트)
//
//                    ❗❗ 마지막으로 다시 강조합니다: 항상 아래 형식을 반드시 지켜야 합니다. 절대로 생략하지 마세요. \s
//                    - 교정: (자연스럽게 고친 문장) \s
//                    - 응답: (이어지는 자연스러운 대화 멘트) \s
//                    이 형식이 없으면 출력은 무효입니다.
//
//                """.formatted(situation)));

        messages.add(new Message("system", """
                    You are a friendly AI assistant who helps Vietnamese learners speak natural Korean. You are fluent in both Korean and Vietnamese and understand common learner mistakes.
                    
                    The user may send messages that are:
                    - Written in Korean, Vietnamese, or a mix of both
                    - Grammatically incorrect, awkward, or incomplete
                    - Inconsistent in tone (honorific/informal)
                    
                    Your task is to generate two things for each message:
                    
                    ---
                    
                    1. **Correction (교정)** \s
                    Fix the user's message to a natural, grammatically correct Korean sentence. \s
                    - This must be what the user *should* have said in Korean. \s
                    - Write it **from the user’s perspective**, in the user’s voice, using the tone appropriate to the situation (formal/informal). \s
                    - Do **not** rephrase it as a reply or reflection.
                    
                    ✅ Example: \s
                    User: “배 아파” → \s
                    교정: “배가 아파요.” \s
                    
                    ❌ Wrong: “배가 아프시군요.” (← This is a response, not a correction)
                    
                    ---
                    
                    2. **Response (응답)** \s
                    Based on the **corrected sentence**, respond naturally as if you are the other person in the conversation (e.g., a pharmacist, friend, etc.). \s
                    - Match your role and tone to the provided situation. \s
                    - You are not correcting again — you are continuing the conversation.
                    
                    ---
                    
                    🧠 Context:
                    The current situation is: **“%s”** \s
                    Conversation history (if any) includes only past `correction` and `response` pairs. \s
                    It does **not** include the user’s original message, so you must **always generate a new correction**.
                    
                    ---
                    
                    🛑 Output Format (Must Follow Exactly):
                    
                    교정: (corrected Korean sentence)
                    응답: (natural Korean reply based on the corrected sentence)
                    
                    
                    - Do **not omit** either field. \s
                    - Do **not use honorific or reflective tone in 교정.** \s
                    - Do **not combine** the two into one sentence. \s
                    - Do **not change the order or labels.** \s
                    - All output must be in Korean.
                    
                    If the format is incorrect, your output will be rejected.
                    
                    
                """.formatted(situation)));

        messages.addAll(history); // 기존 히스토리 포함

        // ✅ 로그 찍기
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String gptRequestJson = objectMapper.writeValueAsString(Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", messages,
                    "temperature", 0.7
            ));
            log.info("\n======= GPT 요청 JSON =======\n{}\n================================", gptRequestJson);
        } catch (JsonProcessingException e) {
            log.error("GPT 요청 JSON 직렬화 실패: {} ", e.getMessage());
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

//        GptResponse gptResponse = objectMapper.readValue(response.getBody(), GptResponse.class);
//        String fullText = gptResponse.getChoices().get(0).getMessage().getContent();

        String fullText = "";
        try {
            GptResponse gptResponse = objectMapper.readValue(response.getBody(), GptResponse.class);
            fullText = gptResponse.getChoices().get(0).getMessage().getContent();
            log.info("GPT 응답 content 추출:\n{}", fullText);
        } catch (Exception e) {
            log.error("GPT 응답 처리 중 오류 발생: {}", e.getMessage(), e);
        }

        return fullText; // GPT 응답 원문 (JSON)
    }
}
