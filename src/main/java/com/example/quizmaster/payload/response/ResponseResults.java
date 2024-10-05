package com.example.quizmaster.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseResults {
    private Long id;
    private Long user;
    private int totalQuestion;
    private int correctAnswers;
    private String timeTaken;
    private Long quiz;
}
