package com.example.quizmaster.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RequestAnswer {
    @NotNull(message = "ID cannot be null")
    @Positive(message = "ID must be a positive value")
    private Long id;

    @NotBlank(message = "Answer text cannot be blank")
    private String text;

    private boolean correct;
}
