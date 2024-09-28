package com.example.quizmaster.payload.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseAnswer {
    private Long id;
    private String text;
    private boolean isCorrect;
    private Long questionId;
}
