package com.project.chatbot.main.Dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinDto {
    String userId;
    String name;
    String email;
    String password;
}
