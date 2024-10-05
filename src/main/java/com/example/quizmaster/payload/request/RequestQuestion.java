package com.example.quizmaster.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RequestQuestion {
    @NotBlank(message = "Question text cannot be blank")
    private String text;

    @NotNull(message = "Quiz ID cannot be null")
    @Positive(message = "Quiz ID must be a positive value")
    private Long quizId;

    @NotEmpty(message = "Answer list cannot be empty")
    private List<RequestAnswer> answerList;
}
