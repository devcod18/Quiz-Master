package com.example.quizmaster.repository;

import com.example.quizmaster.entity.Question;
import com.example.quizmaster.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByQuizId(Long quizId);
    List<Question> findAllByQuizIdIn(List<Long> quizIds);
}
