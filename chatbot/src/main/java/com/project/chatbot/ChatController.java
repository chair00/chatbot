package com.project.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.chatbot.dto.ChatRequest;
import com.project.chatbot.dto.ChatResponse;
import com.project.chatbot.dto.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final GptService gptService;
    private final ChatMemoryStore memoryStore;

    public ChatController(GptService gptService, ChatMemoryStore memoryStore) {
        this.gptService = gptService;
        this.memoryStore = memoryStore;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) throws JsonProcessingException {
        String sessionId = request.sessionId();
        String message = request.message();

        memoryStore.addMessage(sessionId, new Message("user", message));

        String content = gptService.callGpt(memoryStore.getHistory(sessionId), request.context());

        String correction = "";
        String response = "";

        if (content.contains("교정:") && content.contains("응답:")) {
            int correctionStart = content.indexOf("교정:") + 4;
            int responseStart = content.indexOf("응답:");

            correction = content.substring(correctionStart, responseStart).trim();
            response = content.substring(responseStart + 4).trim();
        } else {
            // 혹시 형식이 다르면 fallback
            correction = content;
            response = "(응답 없음)";
        }

        memoryStore.addMessage(sessionId, new Message("assistant", content));

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
