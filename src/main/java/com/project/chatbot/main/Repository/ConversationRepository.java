package com.project.chatbot.main.Repository;

import com.project.chatbot.main.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
}
