package com.example.quizmaster.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseUser {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
