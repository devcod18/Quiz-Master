package com.example.quizmaster.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(@NotBlank @Email String email,
                              @NotBlank @Size(min = 8, max = 15) String password,
                              @NotBlank String firstName,
                              @NotBlank String lastName) {
}
