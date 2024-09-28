package com.example.quizmaster.payload.response;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseQuiz {
    private Long id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private Integer timeLimit;
    private Integer questionCount;
}
