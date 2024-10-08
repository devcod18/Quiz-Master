package com.example.quizmaster.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank
                              @Email(message = "Noto'g'ri email formati. Faqat Gmail manzillari qabul qilinadi!")
                              String email,
                              @NotBlank String password,
                              @NotBlank String firstName,
                              @NotBlank String lastName) {
}
