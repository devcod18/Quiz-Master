package com.example.quizmaster.payload.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqPassTest {
    private Long questionId;
    private Long answerId;
}
