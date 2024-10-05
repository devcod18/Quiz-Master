package com.example.quizmaster.repository;

import com.example.quizmaster.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query("SELECT a FROM Answer a WHERE a.question.id IN :questionIds AND a.isCorrect = true")
    List<Answer> findCorrectAnswersByQuestionIds(@Param("questionIds") List<Long> questionIds);
}
