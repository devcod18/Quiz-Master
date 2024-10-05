package com.example.quizmaster.payload;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email,
                           @NotBlank String password) {

}
