package com.example.quizmaster.payload;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String email,
                                  @NotBlank String password,
                                  @NotBlank String firstName,
                                  @NotBlank String lastName) {
}
