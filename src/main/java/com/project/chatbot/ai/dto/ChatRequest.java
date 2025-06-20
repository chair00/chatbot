package com.project.chatbot.ai.dto;

import java.util.List;

public record ChatRequest(String situation, List<Message> history, String message) {
}
