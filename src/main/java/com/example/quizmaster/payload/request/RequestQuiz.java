package com.example.quizmaster.payload.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RequestQuiz {
    private String title;
    private String description;
    private Integer timeLimit;
    private Integer questionCount;
}
