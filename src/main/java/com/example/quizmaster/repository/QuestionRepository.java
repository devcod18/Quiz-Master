package com.example.quizmaster.repository;

import com.example.quizmaster.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByQuizId(Long quizId);
    List<Question> findAllByQuizIdIn(List<Long> quizIds);
}
