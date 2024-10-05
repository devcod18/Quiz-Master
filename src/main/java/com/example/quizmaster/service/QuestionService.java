package com.example.quizmaster.service;

import com.example.quizmaster.entity.Answer;
import com.example.quizmaster.entity.Question;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.CustomPageable;
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
                        .orElse(null))
                .build();

        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = new ArrayList<>();
        int correctAnswerCount = 0;

        for (RequestAnswer answer : requestQuestion.getAnswerList()) {
            Answer answerEntity = Answer.builder()
                    .answerText(answer.getText())
                    .isCorrect(answer.isCorrect())
                    .question(savedQuestion)
                    .build();

            if (answer.isCorrect()) {
                correctAnswerCount++;
            }

            answers.add(answerEntity);
            answerRepository.save(answerEntity);
        }

        if (correctAnswerCount != 1) {
            return new ApiResponse("Only one answer must be marked as correct!", HttpStatus.BAD_REQUEST);
        }

        savedQuestion.setAnswers(answers);

        return new ApiResponse("Question saved successfully!", HttpStatus.CREATED);
    }

    public ApiResponse getAllQuestion(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findAll(pageRequest);
        List<ResponseQuestion> responseQuestions = toResponseQuestion(questionPage.getContent());

        CustomPageable pageableResponse = CustomPageable.builder()
                .size(size)
                .page(page)
                .totalPage(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .data(responseQuestions)
                .build();

        return new ApiResponse("Questions retrieved successfully!", HttpStatus.OK, pageableResponse);

    }

    public ApiResponse updateQuestion(Long id, RequestQuestion requestQuestion) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return new ApiResponse("Question not found!", HttpStatus.NOT_FOUND);
        }

        question.setQuestion_text(requestQuestion.getText());
        question.setQuiz(quizRepository.findById(requestQuestion.getQuizId()).orElse(null));
        if (question.getQuiz() == null) {
            return new ApiResponse("Quiz not found!", HttpStatus.NOT_FOUND);
        }

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

        return new ApiResponse("Question updated successfully!", HttpStatus.OK);
    }

    public ApiResponse deleteQuestion(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return new ApiResponse("Question not found!", HttpStatus.NOT_FOUND);
        }

        answerRepository.deleteAll(question.getAnswers());

        questionRepository.delete(question);

        return new ApiResponse("Question deleted successfully!", HttpStatus.OK);
    }

    public ApiResponse getOne(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return new ApiResponse("Question not found!", HttpStatus.NOT_FOUND);
        }

        ResponseQuestion responseQuestion = (ResponseQuestion) toResponseQuestion((List<Question>) question);

        return new ApiResponse("Question retrieved successfully!", HttpStatus.OK, responseQuestion);
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
                        .questionId(answer.getQuestion().getId())
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
