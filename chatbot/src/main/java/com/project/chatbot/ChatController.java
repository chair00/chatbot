package com.project.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.chatbot.dto.ChatRequest;
import com.project.chatbot.dto.ChatResponse;
import com.project.chatbot.dto.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/ai/generate")
@Slf4j
public class ChatController {

    private final GptService gptService;
    private final ChatMemoryStore memoryStore;

    public ChatController(GptService gptService, ChatMemoryStore memoryStore) {
        this.gptService = gptService;
        this.memoryStore = memoryStore;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) throws JsonProcessingException {
        String message = request.message();
        List<Message> history = request.history();

        history.add(new Message("user", message));

        String content = gptService.callGpt(history, request.situation());

        String correction = "";
        String response = "";

        if (content.contains("교정:") && content.contains("응답:")) {
            int correctionStart = content.indexOf("교정:") + 4;
            int responseStart = content.indexOf("응답:");

            correction = content.substring(correctionStart, responseStart).trim();
            response = content.substring(responseStart + 4).trim();
        } else {
            log.warn("GPT 응답 형식이 이상함: {}", content);
            correction = content;
            response = "(응답 없음)";
        }

        return ResponseEntity.ok(new ChatResponse(
                message,
                correction,
                response,
                LocalDateTime.now().toString()
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset(@RequestBody Map<String, String> req) {
        memoryStore.resetHistory(req.get("sessionId"));
        return ResponseEntity.ok().build();
    }
}
