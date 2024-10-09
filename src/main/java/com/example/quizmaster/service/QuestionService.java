package com.example.quizmaster.service;

import com.example.quizmaster.entity.Answer;
import com.example.quizmaster.entity.Question;
import com.example.quizmaster.entity.Quiz;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.CustomPageable;
import com.example.quizmaster.payload.request.RequestAnswer;
import com.example.quizmaster.payload.request.RequestQuestion;
import com.example.quizmaster.payload.response.ResponseAnswer;
import com.example.quizmaster.payload.response.ResponseQuestion;
import com.example.quizmaster.repository.AnswerRepository;
import com.example.quizmaster.repository.QuestionRepository;
import com.example.quizmaster.repository.QuizRepository;
import com.example.quizmaster.repository.UserAnswerRepository;
import jakarta.transaction.Transactional;
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

    private final UserAnswerRepository userAnswerRepository;

    // Savolni saqlash
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
            return new ApiResponse("Faqat bitta javob to'g'ri belgilanishi kerak!", HttpStatus.BAD_REQUEST);
        }

        savedQuestion.setAnswers(answers);
        return new ApiResponse("Savol muvaffaqiyatli saqlandi!", HttpStatus.CREATED);
    }

    // Barcha savollarni olish
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

        return new ApiResponse("Savollar muvaffaqiyatli olindi!", HttpStatus.OK, pageableResponse);
    }

    // Savolni yangilash
    public ApiResponse updateQuestion(Long id, RequestQuestion requestQuestion) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return new ApiResponse("Savol topilmadi!", HttpStatus.NOT_FOUND);
        }

        question.setQuestion_text(requestQuestion.getText());
        question.setQuiz(quizRepository.findById(requestQuestion.getQuizId()).orElse(null));
        if (question.getQuiz() == null) {
            return new ApiResponse("Quiz topilmadi!", HttpStatus.NOT_FOUND);
        }

        List<Answer> updatedAnswers = new ArrayList<>();
        int correctAnswerCount = 0; // To'g'ri javob sonini hisoblash

        for (RequestAnswer requestAnswer : requestQuestion.getAnswerList()) {
            Answer answer = new Answer();
            answer.setAnswerText(requestAnswer.getText());
            answer.setIsCorrect(requestAnswer.isCorrect());
            answer.setQuestion(question);

            if (requestAnswer.isCorrect()) {
                correctAnswerCount++;
            }

            updatedAnswers.add(answerRepository.save(answer));
        }

        // To'g'ri javob faqat bitta bo'lishini tekshirish
        if (correctAnswerCount != 1) {
            return new ApiResponse("Faqat bitta javob to'g'ri bo'lishi kerak!", HttpStatus.BAD_REQUEST);
        }

        List<Answer> existingAnswers = question.getAnswers();
        existingAnswers.stream()
                .filter(existingAnswer -> updatedAnswers.stream()
                        .noneMatch(updatedAnswer -> updatedAnswer.getId().equals(existingAnswer.getId())))
                .forEach(answerRepository::delete);

        question.setAnswers(updatedAnswers);
        questionRepository.save(question);

        return new ApiResponse("Savol muvaffaqiyatli yangilandi!", HttpStatus.OK);
    }


    // Savolni o'chirish
    @Transactional
    public ApiResponse deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return new ApiResponse("Savol topilmadi!", HttpStatus.NOT_FOUND);
        }

        // Savolga bog'liq barcha javoblarni o'chirish
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        answerRepository.deleteAll(answers);

        // Savolni o'chirish
        questionRepository.delete(question);

        return new ApiResponse("Savol va unga tegishli javoblar o'chirildi!", HttpStatus.OK);
    }


    // Bitta savolni olish
    public ApiResponse getOne(Long id) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return new ApiResponse("Savol topilmadi!", HttpStatus.NOT_FOUND);
        }

        ResponseQuestion responseQuestion = toResponseQuestion(question);
        return new ApiResponse("Savol muvaffaqiyatli olindi!", HttpStatus.OK, responseQuestion);
    }

    // Savollarni Response formatiga o'zgartirish
    public List<ResponseQuestion> toResponseQuestion(List<Question> questions) {
        List<ResponseQuestion> responseQuestions = new ArrayList<>();
        for (Question question : questions) {
            responseQuestions.add(toResponseQuestion(question));
        }
        return responseQuestions;
    }

    // Bitta savolni Response formatiga o'zgartirish
    public ResponseQuestion toResponseQuestion(Question question) {
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
        return ResponseQuestion.builder()
                .id(question.getId())
                .text(question.getQuestion_text())
                .answers(responseAnswers)
                .quizId(question.getQuiz().getId())
                .build();
    }


}
