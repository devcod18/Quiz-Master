package com.example.quizmaster.controller;

import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.request.RequestQuestion;
import com.example.quizmaster.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
@Tag(name = "Question Controller", description = "Test savollarini boshqarish bilan bog'liq operatsiyalar")
public class QuestionController {

    private final QuestionService questionService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @Operation(summary = "Yangi savol saqlash", description = "Test tizimida yangi savolni saqlash imkonini beradi")
    @PostMapping("/saveQuestion")
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody RequestQuestion question) {
        ApiResponse apiResponse = questionService.saveQuestion(question);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @Operation(summary = "Barcha savollarni olish", description = "Barcha test savollarining sahifa bo'yicha ro'yxatini olish")
    @GetMapping("/getAllQuestion")
    public ResponseEntity<ApiResponse> getAllQuestions(@Valid @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = questionService.getAllQuestion(page, size);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Savolni yangilash", description = "Berilgan ID orqali mavjud savolni yangilaydi")
    @PutMapping("/updateQuestion/{questionId}")
    public ResponseEntity<ApiResponse> updateQuestion(@Valid @PathVariable Long questionId,
                                                      @RequestBody RequestQuestion requestQuestion) {
        ApiResponse response = questionService.updateQuestion(questionId, requestQuestion);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Savolni o'chirish", description = "Berilgan ID orqali test tizimidan savolni o'chiradi")
    @DeleteMapping("/deleteQuestion/{questionId}")
    public ResponseEntity<ApiResponse> deleteQuestion(@Valid @PathVariable Long questionId) {
        ApiResponse response = questionService.deleteQuestion(questionId);
        return new ResponseEntity<>(response, response.getCode());
    }

    @GetMapping("/getOne/{questionId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @Operation(summary = "Bir savolni olish", description = "Berilgan savol ID orqali bir savol haqida ma'lumot oladi")
    public ResponseEntity<ApiResponse> getOne(@PathVariable Long questionId) {
        ApiResponse one = questionService.getOne(questionId);
        return new ResponseEntity<>(one, one.getCode());
    }
}
