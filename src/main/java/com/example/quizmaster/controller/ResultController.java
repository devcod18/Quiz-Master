package com.example.quizmaster.controller;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.security.CurrentUser;
import com.example.quizmaster.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag; // Tag annotatsiyasini import qilish
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/natija")
@RequiredArgsConstructor
@Tag(name = "Result Controller", description = "Foydalanuvchi natijalariga oid operatsiyalarni boshqaradi.")
public class ResultController {
    private final ResultService resultService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Foydalanuvchi natijalarini ko'rish", description = "Autentifikatsiyadan o'tgan foydalanuvchi natijalarini qaytaradi.")
    @GetMapping("/natijalarTarixi")
    public ResponseEntity<ApiResponse> getUserResults(@Valid @CurrentUser User user) {
        ApiResponse userResults = resultService.getUserResults(user);
        return ResponseEntity.ok(userResults);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/so'nggiTestNatijasi")
    @Operation(summary = "So'nggi test natijasi", description = "Autentifikatsiyadan o'tgan foydalanuvchining so'nggi test natijasini qaytaradi.")
    public ResponseEntity<ApiResponse> lastTestResult(@Valid @CurrentUser User user) {
        ApiResponse response = resultService.lastTestResult(user);
        return ResponseEntity.ok(response);
    }
}
