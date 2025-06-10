package com.project.chatbot.dto;

public record ChatResponse(String message, String correction, String response, String timestamp) {
}
