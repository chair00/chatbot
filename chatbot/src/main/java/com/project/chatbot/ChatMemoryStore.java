package com.project.chatbot;

import com.project.chatbot.dto.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatMemoryStore {
    private final Map<String, List<Message>> sessionHistory = new ConcurrentHashMap<>();

    public List<Message> getHistory(String sessionId) {
        return sessionHistory.getOrDefault(sessionId, new ArrayList<>());
    }

    public void addMessage(String sessionId, Message message) {
        sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }

    public void resetHistory(String sessionId) {
        sessionHistory.remove(sessionId);
    }
}
