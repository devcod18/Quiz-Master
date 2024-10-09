package com.example.quizmaster.service;

import com.example.quizmaster.entity.Result;
import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.response.ResponseResults;
import com.example.quizmaster.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final ResultRepository resultRepository;

    // Foydalanuvchining barcha test natijalarini olish
    public ApiResponse getUserResults(User user) {
        List<ResponseResults> resultList = resultRepository
                .findByUserIdOrderByIdDesc(user.getId())
                .stream()
                .map(result -> ResponseResults.builder()
                        .id(result.getId())
                        .timeTaken(String.valueOf(result.getTimeTaken()))
                        .correctAnswers(result.getCorrectAnswers())
                        .quiz(result.getQuiz().getId())
                        .totalQuestion(result.getTotalQuestion())
                        .user(result.getUser().getId())
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse("Foydalanuvchi natijalari muvaffaqiyatli olindi", HttpStatus.OK, resultList);
    }

    // Foydalanuvchining so'nggi test natijasini olish
    public ApiResponse lastTestResult(User user) {
        List<Result> allResults = resultRepository.findAllByUserId(user.getId());

        Result latestResult = allResults.stream()
                .max(Comparator.comparing(Result::getId))
                .orElse(null);

        if (latestResult != null) {
            ResponseResults responseResult = ResponseResults.builder()
                    .id(latestResult.getId())
                    .timeTaken(String.valueOf(latestResult.getTimeTaken()))
                    .correctAnswers(latestResult.getCorrectAnswers())
                    .quiz(latestResult.getQuiz().getId())
                    .totalQuestion(latestResult.getTotalQuestion())
                    .user(latestResult.getUser().getId())
                    .build();

            return new ApiResponse("So'nggi test natijasi muvaffaqiyatli olindi", HttpStatus.OK, responseResult);
        } else {
            return new ApiResponse("Foydalanuvchi uchun test natijalari topilmadi", HttpStatus.NOT_FOUND, null);
        }
    }
}
