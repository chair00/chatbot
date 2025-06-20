package com.project.chatbot.main.Entity;

import com.project.chatbot.main.Dto.UserDto.JoinDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String userId;

    private String name;

    private String password;
    private String email;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public User(JoinDto joinDto){
        this.userId = joinDto.getUserId();
        this.name = joinDto.getName();
        this.password = joinDto.getPassword();
        this.email = joinDto.getEmail();
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }


}
