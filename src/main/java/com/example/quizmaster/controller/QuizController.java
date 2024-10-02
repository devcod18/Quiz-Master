package com.example.quizmaster.controller;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.request.ReqPassTest;
import com.example.quizmaster.payload.request.RequestQuiz;
import com.example.quizmaster.security.CurrentUser;
import com.example.quizmaster.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
@Tag(name = "Quiz Controller", description = "Handles operations related to quizzes such as saving, retrieving, updating, and deleting quizzes.")
public class QuizController {

    private final QuizService service;

    @Operation(summary = "Save a quiz", description = "Saves a new quiz based on the provided request data.")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody RequestQuiz requestQuiz) {
        ApiResponse save = service.save(requestQuiz);
        return new ResponseEntity<>(save, save.getCode());
    }

    @Operation(summary = "Retrieve all quizzes", description = "Retrieves a list of all quizzes with pagination options.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        ApiResponse response = service.getAll(page, size);
        return new ResponseEntity<>(response, response.getCode());
    }

    @Operation(summary = "Update a quiz", description = "Updates an existing quiz identified by the given ID with the provided request data.")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody RequestQuiz requestQuiz) {
        ApiResponse response = service.update(id, requestQuiz);
        return new ResponseEntity<>(response, response.getCode());
    }

    @Operation(summary = "Delete a quiz", description = "Deletes a quiz identified by the given ID.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        ApiResponse response = service.delete(id);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Start a test", description = "Allows users to start a test for a specific quiz identified by quiz ID.")
    @GetMapping("/start/test/{quizId}")
    public ResponseEntity<ApiResponse> startTest(@PathVariable Long quizId) {
        ApiResponse apiResponse = service.getRandomQuestionsForQuiz(quizId);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Submit answers for a test", description = "Allows users to submit their answers for a quiz identified by quiz ID.")
    @PostMapping("/passTest/{quizId}")
    public ResponseEntity<ApiResponse> passTest(@RequestBody List<ReqPassTest> reqPassTestList,
                                                @CurrentUser User user,
                                                @PathVariable Long quizId) {
        ApiResponse apiResponse = service.passTest(reqPassTestList, user, quizId);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Get a specific quiz", description = "Retrieves a single quiz by its ID. Only accessible to super admins and admins.")
    @PutMapping("/getOne/{quizId}")
    public ResponseEntity<ApiResponse> getOne(@PathVariable Long quizId){
        ApiResponse one = service.getOne(quizId);
        return ResponseEntity.ok(one);
    }
}
