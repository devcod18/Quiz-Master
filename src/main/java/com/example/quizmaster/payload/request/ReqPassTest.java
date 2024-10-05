package com.example.quizmaster.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqPassTest {
    @NotNull(message = "Question ID cannot be null")
    @Positive(message = "Question ID must be a positive value")
    private Long questionId;

    @NotNull(message = "Answer ID cannot be null")
    @Positive(message = "Answer ID must be a positive value")
    private Long answerId;
}
