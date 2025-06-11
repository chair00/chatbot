package com.project.chatbot.dto;

import java.util.List;

public record ChatRequest(String situation, List<Message> history, String message) {
}
