package com.example.quizmaster.controller;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.request.ReqPassTest;
import com.example.quizmaster.payload.request.RequestQuiz;
import com.example.quizmaster.security.CurrentUser;
import com.example.quizmaster.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
@Tag(name = "Quiz Controller", description = "Testlarni saqlash, olish, yangilash va o'chirish operatsiyalarini boshqaradi.")
public class QuizController {

    private final QuizService service;

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Test saqlash", description = "Berilgan so'rov ma'lumotlari asosida yangi test saqlaydi.")
    @PostMapping("/saveQuiz")
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody RequestQuiz requestQuiz) {
        ApiResponse save = service.save(requestQuiz);
        return new ResponseEntity<>(save, save.getCode());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Barcha testlarni olish", description = "Barcha testlarni sahifa bo'yicha olish.")
    @GetMapping("/getAllQuiz")
    public ResponseEntity<ApiResponse> getAll(
            @Valid @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        ApiResponse response = service.getAll(page, size);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Testni yangilash", description = "Berilgan ID orqali mavjud testni yangilaydi.")
    @PutMapping("/updateQuiz/{quizId}")
    public ResponseEntity<ApiResponse> update(@Valid @PathVariable Long quizId, @RequestBody RequestQuiz requestQuiz) {
        ApiResponse response = service.update(quizId, requestQuiz);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Testni o'chirish", description = "Berilgan ID orqali testni o'chiradi.")
    @DeleteMapping("/deleteQuiz/{quizId}")
    public ResponseEntity<ApiResponse> delete(@Valid @PathVariable Long quizId) {
        ApiResponse response = service.delete(quizId);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Testni boshlash", description = "Foydalanuvchilarga berilgan test ID orqali test boshlash imkonini beradi.")
    @GetMapping("/startTest/{quizId}")
    public ResponseEntity<ApiResponse> startTest(@Valid @PathVariable Long quizId) {
        ApiResponse apiResponse = service.getRandomQuestionsForQuiz(quizId);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Test uchun javoblarni yuborish", description = "Foydalanuvchilarga berilgan test ID uchun javoblarni yuborish imkonini beradi.")
    @PostMapping("/passTest/{quizId}")
    public ResponseEntity<ApiResponse> passTest(@Valid @RequestBody List<ReqPassTest> reqPassTestList,
                                                @CurrentUser User user,
                                                @PathVariable Long quizId,
                                                @RequestParam Long timeTaken) {
        ApiResponse apiResponse = service.passTest(reqPassTestList, user, quizId, timeTaken);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Bir testni ID orqali olish", description = "Berilgan quiz ID orqali bitta test haqida ma'lumot oladi.")
    @GetMapping("/getOneQuiz/{quizId}")
    public ResponseEntity<ApiResponse> getOne(@Valid @PathVariable Long quizId) {
        ApiResponse one = service.getOne(quizId);
        return ResponseEntity.ok(one);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Bir nechta testlarni boshlash", description = "Berilgan quiz IDlar uchun random savollar olish orqali test sessiyasini boshlaydi.")
    @GetMapping("/start-test")
    public ResponseEntity<ApiResponse> multiTest(@Valid @RequestParam List<Long> quizIds) {
        if (quizIds == null || quizIds.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse("Quiz IDlar bo'sh bo'lmasligi kerak!", HttpStatus.BAD_REQUEST));
        }

        ApiResponse apiResponse = service.getRandomQuestionsForMultipleQuizzes(quizIds);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
