package com.example.quizmaster.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Question cannot be null")
    private Question question;

    @NotBlank(message = "Answer text cannot be blank")
    private String answerText;

    @NotNull(message = "isCorrect cannot be null")
    private Boolean isCorrect;
}
