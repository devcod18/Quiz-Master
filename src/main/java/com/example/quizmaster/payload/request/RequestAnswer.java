package com.example.quizmaster.payload.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RequestAnswer {
    private String text;
    private boolean correct;
}
