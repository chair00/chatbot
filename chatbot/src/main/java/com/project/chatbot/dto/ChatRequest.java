package com.project.chatbot.dto;

public record ChatRequest(String sessionId, String context, String message) {
}
