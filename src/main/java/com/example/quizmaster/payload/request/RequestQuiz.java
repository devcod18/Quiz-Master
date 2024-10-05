package com.example.quizmaster.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RequestQuiz {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    private Integer timeLimit;

    @Min(value = 1, message = "Question count must be at least 1")
    private Integer questionCount;
}
