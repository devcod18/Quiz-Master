package com.example.quizmaster.service;

import com.example.quizmaster.entity.*;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.Pageable;
import com.example.quizmaster.payload.request.ReqPassTest;
import com.example.quizmaster.payload.request.RequestQuiz;
import com.example.quizmaster.payload.response.ResponseQuestion;
import com.example.quizmaster.payload.response.ResponseQuiz;
import com.example.quizmaster.repository.AnswerRepository;
import com.example.quizmaster.repository.QuestionRepository;
import com.example.quizmaster.repository.QuizRepository;
import com.example.quizmaster.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;

    public ApiResponse save(RequestQuiz requestQuiz) {

        Quiz quiz = Quiz.builder()
                .title(requestQuiz.getTitle())
                .description(requestQuiz.getDescription())
                .createdAt(LocalDate.now())
                .questionCount(requestQuiz.getQuestionCount())
                .timeLimit(requestQuiz.getTimeLimit())
                .build();

        quizRepository.save(quiz);
        return new ApiResponse("Quiz saved successfully", HttpStatus.CREATED);
    }

    public ApiResponse getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Quiz> quizPage = quizRepository.findAll(pageRequest);

        List<ResponseQuiz> responseQuizList = quizPage.getContent().stream()
                .map(this::mapToResponseQuiz)
                .collect(Collectors.toList());

        Pageable pageable = Pageable.builder()
                .page(page)
                .size(size)
                .totalPage(quizPage.getTotalPages())
                .totalElements(quizPage.getTotalElements())
                .data(responseQuizList)
                .build();

        return new ApiResponse("Quizzes retrieved successfully", HttpStatus.OK, pageable);
    }

    public ApiResponse update(Long id, RequestQuiz requestQuiz) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        quiz.setTitle(requestQuiz.getTitle());
        quiz.setDescription(requestQuiz.getDescription());
        quiz.setQuestionCount(requestQuiz.getQuestionCount());
        quiz.setTimeLimit(requestQuiz.getTimeLimit());

        quizRepository.save(quiz);
        return new ApiResponse("Quiz updated successfully", HttpStatus.OK);
    }

    public ApiResponse delete(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        quizRepository.delete(quiz);
        return new ApiResponse("Quiz deleted successfully", HttpStatus.OK);
    }

    public ApiResponse getRandomQuestionsForQuiz(Long quiz) {
        Quiz quiz1 = quizRepository.findById(quiz).orElse(null);
        if (quiz1 == null) {
            return new ApiResponse("Quiz not found", HttpStatus.NOT_FOUND);
        }

        List<Question> allQuestions = questionRepository.findAllByQuizId(quiz);
        List<ResponseQuestion> responseQuestions = questionService.toResponseQuestion(allQuestions);
        Collections.shuffle(responseQuestions);

        int questionCount = quiz1.getQuestionCount();

        if (responseQuestions.size() > questionCount) {
            return new ApiResponse("Success", HttpStatus.OK, responseQuestions.subList(0, questionCount));
        }

        return new ApiResponse("Insufficient number of questions available", HttpStatus.CONFLICT);
    }


    public ApiResponse passTest(List<ReqPassTest> passTestList, User user, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse("Quiz not found", HttpStatus.NOT_FOUND);
        }

        List<Long> questionIds = passTestList.stream()
                .map(ReqPassTest::getQuestionId)
                .collect(Collectors.toList());

        List<Answer> correctAnswers = answerRepository.findCorrectAnswersByQuestionIds(questionIds);

        long correctCountAnswers = passTestList.stream()
                .filter(reqPassTest -> correctAnswers.stream()
                        .anyMatch(answer -> answer.getId().equals(reqPassTest.getAnswerId())))
                .count();

        LocalDateTime startTime = LocalDateTime.now();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LocalDateTime finishTime = LocalDateTime.now();

        Long timeTakenSeconds = Duration.between(startTime, finishTime).getSeconds();

        Result result = Result.builder()
                .correctAnswers((int) correctCountAnswers)
                .quiz(quiz)
                .totalQuestion(passTestList.size())
                .user(user)
                .startTime(startTime)
                .finishTime(finishTime)
                .timeTaken(timeTakenSeconds)
                .build();

        resultRepository.save(result);
        return new ApiResponse("Test passed successfully", HttpStatus.OK);
    }



    public ApiResponse getOne(Long id){
        Quiz quiz = quizRepository.findById(id).orElse(null);

        if (quiz == null) {
            return new ApiResponse("Quiz not found with this id: " + id, HttpStatus.NOT_FOUND);
        }

        ResponseQuiz responseQuiz = ResponseQuiz.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .createdAt(quiz.getCreatedAt())
                .timeLimit(quiz.getTimeLimit())
                .questionCount(quiz.getQuestionCount())
                .build();

        return new ApiResponse("Quiz found", HttpStatus.OK, responseQuiz);

    }

    private ResponseQuiz mapToResponseQuiz(Quiz quiz) {
        return ResponseQuiz.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .createdAt(quiz.getCreatedAt())
                .timeLimit(quiz.getTimeLimit())
                .questionCount(quiz.getQuestionCount())
                .build();
    }
}
