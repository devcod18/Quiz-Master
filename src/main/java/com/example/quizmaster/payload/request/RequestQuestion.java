package com.example.quizmaster.payload.request;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RequestQuestion {
    private String text;
    private Long quizId;
    private List<RequestAnswer> answerList;
}
