package com.example.quizmaster.repository;

import com.example.quizmaster.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    void deleteByQuestionId(Long questionId);
}
