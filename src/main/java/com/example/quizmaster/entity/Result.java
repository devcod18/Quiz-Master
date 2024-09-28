package com.example.quizmaster.entity;

import jakarta.persistence.*;
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
    private User user;

    @Column(nullable = false)
    private int totalQuestion;

    @Column(nullable = false)
    private int correctAnswers;

    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private Long timeTaken;

    @ManyToOne
    private Quiz quiz;

}