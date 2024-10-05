package com.example.quizmaster.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    private LocalDate createdAt;

    @NotNull(message = "Time limit cannot be null")
    @Min(value = 1, message = "Time limit must be greater than 0")
    @Column(nullable = false)
    private Integer timeLimit;

    @NotNull(message = "Question count cannot be null")
    @Min(value = 1, message = "Question count must be greater than 0")
    @Column(nullable = false)
    private Integer questionCount;
}
