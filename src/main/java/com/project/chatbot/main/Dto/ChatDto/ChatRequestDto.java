package com.project.chatbot.main.Dto.ChatDto;

import com.project.chatbot.main.Enum.Sender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDto {
    int userId;
    String message;
    Sender sender;
    int conversationId;
}
