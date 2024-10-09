package com.example.quizmaster.repository;

import com.example.quizmaster.entity.Question;
import com.example.quizmaster.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz,Long> {
    Page<Quiz> findAllByOrderByUpdatedAtDesc(PageRequest pageRequest);
}
