package com.example.quizmaster.repository;

import com.example.quizmaster.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findAllByUserId(Long userId);
}
