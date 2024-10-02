package com.example.quizmaster.service;

import com.example.quizmaster.entity.Answer;
import com.example.quizmaster.entity.Question;
import com.example.quizmaster.exception.ResourceNotFoundException;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.Pageable;
import com.example.quizmaster.payload.request.RequestAnswer;
import com.example.quizmaster.payload.request.RequestQuestion;
import com.example.quizmaster.payload.response.ResponseAnswer;
import com.example.quizmaster.payload.response.ResponseQuestion;
import com.example.quizmaster.repository.AnswerRepository;
import com.example.quizmaster.repository.QuestionRepository;
import com.example.quizmaster.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    public ApiResponse saveQuestion(RequestQuestion requestQuestion) {
        Question question = Question.builder()
                .question_text(requestQuestion.getText())
                .quiz(quizRepository.findById(requestQuestion.getQuizId())
                        .orElseThrow(() -> new ResourceNotFoundException("Quiz not found")))
                .build();

        Question save = questionRepository.save(question);

        List<Answer> answers = new ArrayList<>();
        for (RequestAnswer answer : requestQuestion.getAnswerList()) {
            Answer answer1 = Answer.builder()
                    .answerText(answer.getText())
                    .isCorrect(answer.isCorrect())
                    .question(save)
                    .build();
            answers.add(answer1);

            answerRepository.save(answer1);
        }
        question.setAnswers(answers);

        return new ApiResponse("Question saved successfully", HttpStatus.CREATED);
    }

    public ApiResponse getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findAll(pageRequest);
        List<ResponseQuestion> responseQuestions = toResponseQuestion(questionPage.getContent());

        Pageable pageableResponse = Pageable.builder()
                .size(size)
                .page(page)
                .totalPage(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .data(responseQuestions)
                .build();

        return new ApiResponse("Questions retrieved successfully", HttpStatus.OK, pageableResponse);

    }

    public ApiResponse updateQuestion(Long id, RequestQuestion requestQuestion) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        question.setQuestion_text(requestQuestion.getText());
        question.setQuiz(quizRepository.findById(requestQuestion.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found")));

        List<Answer> updatedAnswers = new ArrayList<>();
        for (RequestAnswer requestAnswer : requestQuestion.getAnswerList()) {
            Answer answer;

            if (requestAnswer.getId() != null) {
                answer = answerRepository.findById(requestAnswer.getId())
                        .orElse(new Answer());
            } else {
                answer = new Answer();
            }

            answer.setAnswerText(requestAnswer.getText());
            answer.setIsCorrect(requestAnswer.isCorrect());
            answer.setQuestion(question);

            updatedAnswers.add(answerRepository.save(answer));
        }

        List<Answer> existingAnswers = question.getAnswers();
        existingAnswers.stream()
                .filter(existingAnswer -> updatedAnswers.stream()
                        .noneMatch(updatedAnswer -> updatedAnswer.getId().equals(existingAnswer.getId())))
                .forEach(answerRepository::delete);

        question.setAnswers(updatedAnswers);

        questionRepository.save(question);

        return new ApiResponse("Question updated successfully", HttpStatus.OK);
    }


    public ApiResponse deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        answerRepository.deleteAll(question.getAnswers());

        questionRepository.delete(question);

        return new ApiResponse("Question deleted successfully", HttpStatus.OK);
    }

    public ApiResponse getOne(Long id, List<Question> questions) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return new ApiResponse("Question not found", HttpStatus.NOT_FOUND);
        }

        List<ResponseQuestion> responseQuestions = new ArrayList<>();

        for (Question question1 : questions) {
            List<ResponseAnswer> responseAnswers = new ArrayList<>();

            for (Answer answer : question1.getAnswers()) {
                ResponseAnswer responseAnswer = ResponseAnswer.builder()
                        .id(answer.getId())
                        .text(answer.getAnswerText())
                        .isCorrect(answer.getIsCorrect())
                        .build();

                responseAnswers.add(responseAnswer);
            }

            ResponseQuestion responseQuestion = ResponseQuestion.builder()
                    .id(question1.getId())
                    .text(question1.getQuestion_text())
                    .answers(responseAnswers)
                    .quizId(question1.getQuiz().getId())
                    .build();

            responseQuestions.add(responseQuestion);
        }

        return new ApiResponse("Success", HttpStatus.OK, responseQuestions);
    }


    public List<ResponseQuestion> toResponseQuestion(List<Question> questions) {
        List<ResponseQuestion> responseQuestions = new ArrayList<>();

        for (Question question : questions) {
            List<ResponseAnswer> responseAnswers = new ArrayList<>();

            for (Answer answer : question.getAnswers()) {
                ResponseAnswer responseAnswer = ResponseAnswer.builder()
                        .id(answer.getId())
                        .text(answer.getAnswerText())
                        .isCorrect(answer.getIsCorrect())
                        .build();

                responseAnswers.add(responseAnswer);
            }

            ResponseQuestion responseQuestion = ResponseQuestion.builder()
                    .id(question.getId())
                    .text(question.getQuestion_text())
                    .answers(responseAnswers)
                    .quizId(question.getQuiz().getId())
                    .build();

            responseQuestions.add(responseQuestion);
        }

        return responseQuestions;
    }
}
