package com.example.quizmaster.controller;

import com.example.quizmaster.entity.Question;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/question")
@Tag(name = "Question Controller",
        description = "Operations related to managing quiz questions")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "Save a new question",
            description = "Allows saving a new question in the quiz system")
    @PostMapping("/saveQuestion")
    public ResponseEntity<ApiResponse> save(@Valid @RequestBody RequestQuestion question) {
        ApiResponse apiResponse = questionService.saveQuestion(question);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @Operation(summary = "Get all questions",
            description = "Retrieve a paginated list of all quiz questions")
    @GetMapping("/getAllQuestion")
    public ResponseEntity<ApiResponse> getAllQuestions(
            @Valid @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = questionService.getAll(page, size);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a question",
            description = "Update an existing question by its ID")
    @PutMapping("/updateQuestion{questionId}")
    public ResponseEntity<ApiResponse> updateQuestion(@Valid @PathVariable Long questionId,
                                                      @RequestBody RequestQuestion requestQuestion) {
        ApiResponse response = questionService.updateQuestion(questionId, requestQuestion);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a question",
            description = "Delete a question from the quiz system by its ID")
    @DeleteMapping("/deleteQuestion{questionId}")
    public ResponseEntity<ApiResponse> deleteQuestion(@Valid @PathVariable Long questionId) {
        ApiResponse response = questionService.deleteQuestion(questionId);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasAnyRole('ROLE_USER,ROLE_ADMIN,ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get question by ID",
            description = "Retrieves a question's details by its ID for authorized users.")
    @GetMapping("/getOne/{questionId}")
    public ResponseEntity<ApiResponse> getOneQuestion(@Valid @PathVariable Long questionId,
                                                      @RequestParam List<Question> questions) {
        ApiResponse response = questionService.getOne(questionId);
        return new ResponseEntity<>(response, response.getCode());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @Operation(summary = "Get a question by ID",
            description = "Retrieve a specific question from the quiz system by its ID")
    @GetMapping("/getOneQuestion{questionId}")
    public ResponseEntity<ApiResponse> getOneQuestion(@Valid @PathVariable Long questionId) {
        ApiResponse apiResponse = questionService.getOne(questionId);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }
}
