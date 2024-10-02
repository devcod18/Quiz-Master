package com.example.quizmaster.controller;

import com.example.quizmaster.entity.Question;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.request.RequestQuestion;
import com.example.quizmaster.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
@Tag(name = "Question Controller", description = "Operations related to managing quiz questions")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "Save a new question", description = "Allows saving a new question in the quiz system")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(
            @RequestBody RequestQuestion question) {
        ApiResponse apiResponse = questionService.saveQuestion(question);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @Operation(summary = "Get all questions", description = "Retrieve a paginated list of all quiz questions")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = questionService.getAll(page, size);
        return new ResponseEntity<>(response, response.getCode());
    }

    @Operation(summary = "Update a question", description = "Update an existing question by its ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> updateQuestion(@PathVariable Long id,
                                                      @RequestBody RequestQuestion requestQuestion) {
        ApiResponse response = questionService.updateQuestion(id, requestQuestion);
        return new ResponseEntity<>(response, response.getCode());
    }

    @Operation(summary = "Delete a question", description = "Delete a question from the quiz system by its ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteQuestion(
            @PathVariable Long id) {
        ApiResponse response = questionService.deleteQuestion(id);
        return new ResponseEntity<>(response, response.getCode());
    }

    @Operation(summary = "Get a specific question", description = "Retrieves a single question by its ID, with optional filtering based on a list of questions")
    @GetMapping("/getOne/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_ADMIN,ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse> getOneQuestion(@PathVariable Long id, @RequestParam List<Question> questions) {
        ApiResponse response = questionService.getOne(id, questions);
        return new ResponseEntity<>(response, response.getCode());
    }
}
