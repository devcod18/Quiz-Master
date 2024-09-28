package com.example.quizmaster.service;

import com.example.quizmaster.entity.Result;
import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.response.ResponseResults;
import com.example.quizmaster.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final ResultRepository resultRepository;

    public ApiResponse getUserResults(User user) {
        List<ResponseResults> resultList = resultRepository
                .findAllByUserId(user.getId())
                .stream()
                .map(result -> ResponseResults.builder()
                        .id(result.getId())
                        .timeTaken(result.getTimeTaken())
                        .correctAnswers(result.getCorrectAnswers())
                        .quiz(result.getQuiz().getId())
                        .totalQuestion(result.getTotalQuestion())
                        .user(result.getUser().getId())
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse("Successfully retrieved results", HttpStatus.OK, resultList);
    }

}