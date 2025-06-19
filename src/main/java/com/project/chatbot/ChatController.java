package com.project.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.chatbot.dto.ChatRequest;
import com.project.chatbot.dto.ChatResponse;
import com.project.chatbot.dto.Message;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final GptService gptService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) throws JsonProcessingException {
        String message = request.message();
        List<Message> history = request.history();

        history.add(new Message("user", message));
        String content = gptService.callGpt(history, request.situation());

        String correction = GptParser.extractCorrection(content);
        String response = GptParser.extractResponse(content);

        return ResponseEntity.ok(new ChatResponse(
                message,
                correction,
                response,
                LocalDateTime.now().toString()
        ));
    }
}
