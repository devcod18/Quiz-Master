package com.example.quizmaster.payload.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ResponseQuestion {
    private Long id;
    private String text;
    private List<ResponseAnswer> answers;
    private Long quizId;
}
