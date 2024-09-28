package com.example.quizmaster.repository;

import com.example.quizmaster.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz,Long> {

}
