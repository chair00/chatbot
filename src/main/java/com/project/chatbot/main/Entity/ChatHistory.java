package com.project.chatbot.main.Entity;

import com.project.chatbot.main.Dto.ChatDto.ChatRequestDto;
import com.project.chatbot.main.Enum.Sender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;

    @Lob
    @Column(columnDefinition = "LONGTEXT",nullable = false)
    private String message;

    private Sender sender;

    @CreationTimestamp
    private Timestamp createdAt;

    private int conversationId;

    public ChatHistory(ChatRequestDto chatRequestDto) {
        this.userId = chatRequestDto.getUserId();
        this.message = chatRequestDto.getMessage();
        this.conversationId = chatRequestDto.getConversationId();
        this.sender = chatRequestDto.getSender();
    }

}
