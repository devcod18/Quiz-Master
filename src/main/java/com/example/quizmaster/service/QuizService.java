package com.example.quizmaster.service;

import com.example.quizmaster.entity.*;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.CustomPageable;
import com.example.quizmaster.payload.request.ReqPassTest;
import com.example.quizmaster.payload.request.RequestQuiz;
import com.example.quizmaster.payload.response.ResponseQuestion;
import com.example.quizmaster.payload.response.ResponseQuiz;
import com.example.quizmaster.repository.AnswerRepository;
import com.example.quizmaster.repository.QuestionRepository;
import com.example.quizmaster.repository.QuizRepository;
import com.example.quizmaster.repository.ResultRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;


    // Yangi testni saqlash
    public ApiResponse saveQuiz(RequestQuiz requestQuiz) {
        Quiz quiz = Quiz.builder()
                .title(requestQuiz.getTitle())
                .description(requestQuiz.getDescription())
                .createdAt(LocalDateTime.now())
                .questionCount(requestQuiz.getQuestionCount())
                .timeLimit(requestQuiz.getTimeLimit())
                .build();

        quizRepository.save(quiz);
        return new ApiResponse("Test muvaffaqiyatli saqlandi!", HttpStatus.CREATED);
    }

    // Barcha testlarni olish
    public ApiResponse getAllQuiz(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Quiz> quizPage = quizRepository.findAllByOrderByUpdatedAtDesc(pageRequest);
        List<ResponseQuiz> responseQuizzes = quizPage.getContent().stream()
                .map(this::mapToResponseQuiz)
                .collect(Collectors.toList());

        CustomPageable pageableResponse = CustomPageable.builder()
                .size(size)
                .page(page)
                .totalPage(quizPage.getTotalPages())
                .totalElements(quizPage.getTotalElements())
                .data(responseQuizzes)
                .build();

        return new ApiResponse("Quizzes muvaffaqiyatli olindi!", HttpStatus.OK, pageableResponse);
    }

    // Testni yangilash
    public ApiResponse updateQuiz(Long id, RequestQuiz requestQuiz) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return new ApiResponse("Test topilmadi!", HttpStatus.NOT_FOUND);
        }

        quiz.setTitle(requestQuiz.getTitle());
        quiz.setDescription(requestQuiz.getDescription());
        quiz.setQuestionCount(requestQuiz.getQuestionCount());
        quiz.setTimeLimit(requestQuiz.getTimeLimit());

        quizRepository.save(quiz);
        return new ApiResponse("Test muvaffaqiyatli yangilandi!", HttpStatus.OK);
    }

    // Testni o'chirish
    public ApiResponse deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return new ApiResponse("Test topilmadi!", HttpStatus.NOT_FOUND);
        }

        quizRepository.delete(quiz);
        return new ApiResponse("Test muvaffaqiyatli o'chirildi!", HttpStatus.OK);
    }

    // Test uchun tasodifiy savollarni olish
    public ApiResponse getRandomQuestionsForQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse("Test topilmadi!", HttpStatus.NOT_FOUND);
        }

        List<Question> allQuestions = questionRepository.findAllByQuizId(quizId);
        if (allQuestions.isEmpty()) {
            return new ApiResponse("Quizga tegishli savollar mavjud emas!", HttpStatus.NOT_FOUND);
        }

        List<ResponseQuestion> responseQuestions = questionService.toResponseQuestion(allQuestions);
        Collections.shuffle(responseQuestions);

        int questionCount = quiz.getQuestionCount();

        if (responseQuestions.size() >= questionCount) {
            return new ApiResponse("Muvaffaqiyat!", HttpStatus.OK, responseQuestions.subList(0, questionCount));
        }

        return new ApiResponse("Yetarli savollar mavjud emas!", HttpStatus.OK, responseQuestions);
    }

    // testni otkazish
    public ApiResponse passTest(List<ReqPassTest> passTestList, User user, Long quizId, Long timeTaken) {
        Quiz quiz = quizRepository.findById(quizId).orElse(null);
        if (quiz == null) {
            return new ApiResponse("Test topilmadi!", HttpStatus.NOT_FOUND);
        }

        LocalDateTime startTime = LocalDateTime.now();

        List<Long> questionIds = passTestList.stream()
                .map(ReqPassTest::getQuestionId)
                .collect(Collectors.toList());

        List<Answer> correctAnswers = answerRepository.findCorrectAnswersByQuestionIds(questionIds);

        long correctCountAnswers = passTestList.stream()
                .filter(reqPassTest -> correctAnswers.stream()
                        .anyMatch(answer -> answer.getId().equals(reqPassTest.getAnswerId())))
                .count();

        LocalDateTime endTime = LocalDateTime.now();

        Result result = Result.builder()
                .correctAnswers((int) correctCountAnswers)
                .quiz(quiz)
                .totalQuestion(passTestList.size())
                .user(user)
                .timeTaken(timeTaken)
                .createdAt(LocalDateTime.now())
                .endTime(endTime)
                .startTime(startTime)
                .build();

        resultRepository.save(result);
        return new ApiResponse("Test muvaffaqiyatli o'tkazildi!", HttpStatus.OK);
    }

    // Bitta testni olish
    public ApiResponse getOneQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz == null) {
            return new ApiResponse("Test topilmadi!", HttpStatus.NOT_FOUND);
        }

        ResponseQuiz responseQuiz = ResponseQuiz.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .createdAt(quiz.getCreatedAt())
                .timeLimit(quiz.getTimeLimit())
                .questionCount(quiz.getQuestionCount())
                .updatedAt(quiz.getUpdatedAt())
                .build();

        return new ApiResponse("Test topildi!", HttpStatus.OK, responseQuiz);
    }

    // Bir nechta testlar uchun tasodifiy savollarni olish
    public ApiResponse getRandomQuestionsForMultipleQuizzes(List<Long> quizIds, Map<Long, Integer> quizSizes) {
        // Berilgan quiz identifikatorlari bo'yicha quizlarni ma'lumotlar bazasidan olish
        List<Quiz> quizzes = quizRepository.findAllById(quizIds);

        // Agar hech qanday quiz topilmasa, xato xabarini qaytarish
        if (quizzes.isEmpty()) {
            return new ApiResponse("Bir yoki bir necha test topilmadi!", HttpStatus.NOT_FOUND);
        }

        // Barcha savollarni saqlash uchun ro'yxat yaratish
        List<Question> allQuestions = new ArrayList<>();

        // Har bir quiz uchun savollarni olish
        for (Quiz quiz : quizzes) {
            // Har bir quiz uchun kiritilgan savol sonini olish, default qiymatda 0 boladi
            int size = quizSizes.getOrDefault(quiz.getId(), 0);

            // Har bir quiz bo'yicha savollarni olish
            List<Question> questions = questionRepository.findAllByQuizId(quiz.getId());

            // Agar savollar mavjud bo'lmasa, keyingi quizga o'tish
            if (questions.isEmpty()) {
                continue;
            }

            // Savol sonini tekshirish va moslashtirish
            if (size <= 0) {
                size = questions.size(); // Agar 0 yoki manfiy bo'lsa, barcha savollar olinadi
            } else if (size > questions.size()) {
                size = questions.size(); // Agar so'ragan son mavjud savollardan katta bo'lsa, mavjud savollarni olish
            }

            // Savollarni aralashtirish
            Collections.shuffle(questions);

            // Tasodifiy savollarni olish
            List<Question> randomQuestions = questions.subList(0, size);

            // Tasodifiy savollarni umumiy ro'yxatga qo'shish
            allQuestions.addAll(randomQuestions);
        }

        // Savollarni ResponseQuestion formatiga o'tkazish
        List<ResponseQuestion> responseQuestions = questionService.toResponseQuestion(allQuestions);

        return new ApiResponse("Muvaffaqiyat!", HttpStatus.OK, responseQuestions);
    }


    // Testlarni javob formatiga o'zgartirish
    private ResponseQuiz mapToResponseQuiz(Quiz quiz) {
        return ResponseQuiz.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .timeLimit(quiz.getTimeLimit())
                .questionCount(quiz.getQuestionCount())
                .build();
    }





}
