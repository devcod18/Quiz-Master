package com.example.quizmaster.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User cannot be null")
    private User user;

    @NotNull(message = "Total question cannot be null")
    @Min(value = 1, message = "Total question must be greater than 0")
    @Column(nullable = false)
    private int totalQuestion;

    @NotNull(message = "Correct answers cannot be null")
    @Min(value = 0, message = "Correct answers cannot be negative")
    @Column(nullable = false)
    private int correctAnswers;

    @NotNull(message = "Start time cannot be null")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "End time cannot be null")
    @Column(nullable = false)
    private LocalDateTime endTime;

    @NotNull(message = "Time taken cannot be null")
    @Min(value = 1, message = "Time taken must be greater than 0")
    @Column(nullable = false)
    private Long timeTaken;

    @ManyToOne
    @NotNull(message = "Quiz cannot be null")
    private Quiz quiz;
}
