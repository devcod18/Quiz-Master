package com.example.quizmaster.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Question cannot be null")
    @ManyToOne
    private Question question;

    @NotNull(message = "Answer cannot be null")
    @ManyToOne
    private Answer answer;

    @NotNull(message = "Result cannot be null")
    @ManyToOne
    private Result result;
}
