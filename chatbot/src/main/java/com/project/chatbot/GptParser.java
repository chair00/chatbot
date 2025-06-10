package com.project.chatbot;

public class GptParser {
    public static String extractCorrection(String content) {
        return extract(content, "교정:");
    }

    public static String extractResponse(String content) {
        return extract(content, "응답:");
    }

    private static String extract(String text, String prefix) {
        int start = text.indexOf(prefix);
        if (start == -1) return "";
        int end = text.indexOf("\n", start + prefix.length());
        return text.substring(start + prefix.length(), end != -1 ? end : text.length()).trim();
    }
}
