package com.example.quizmaster.controller;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.security.CurrentUser;
import com.example.quizmaster.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; // Import the Tag annotation
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/result")
@RequiredArgsConstructor
@Tag(name = "Result Controller", description = "Handles operations related to user results.")
public class ResultController {
    private final ResultService resultService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieve user results", description = "Fetches the results of the authenticated user.")
    @GetMapping("/resultHistory")
    public ResponseEntity<ApiResponse> getUserResults(@Valid @CurrentUser User user) {
        ApiResponse userResults = resultService.getUserResults(user);
        return ResponseEntity.ok(userResults);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("getTestResult")
    @Operation(summary = "Last test result",description = "Fetches the results of the authenticated user.")
    public ResponseEntity<ApiResponse> lastTestResult(@CurrentUser User user) {
        ApiResponse response = resultService.lastTestResult(user);
        return ResponseEntity.ok(response);
    }
}
