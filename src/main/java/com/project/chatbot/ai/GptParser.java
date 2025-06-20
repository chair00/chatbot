package com.project.chatbot.ai;

public class GptParser {
    public static String extractCorrection(String content) {
        return extract(content, "교정:", "(교정 없음)");
    }

    public static String extractResponse(String content) {
        return extract(content, "응답:", "(응답 없음)");
    }

    private static String extract(String text, String prefix, String defaultMessage) {
        int start = text.indexOf(prefix);
        if (start == -1) return defaultMessage;
        int end = text.indexOf("\n", start + prefix.length());
        return text.substring(start + prefix.length(), end != -1 ? end : text.length()).trim();
    }
}
