package com.example.quizmaster.controller;

import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.LoginRequest;
import com.example.quizmaster.payload.RegisterRequest;
import com.example.quizmaster.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management",
        description = "APIs for user and admin registration, login, and activation.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
            description = "Allows anyone to register a new user with necessary details.")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = authService.registerUser(request);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Allows a user to log in by providing their email and password.")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        ApiResponse apiResponse = authService.login(request);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @PutMapping("/check-code")
    @Operation(summary = "Check activation code",
            description = "Checks the activation code sent to the user's email.")
    public ResponseEntity<ApiResponse> checkCode(@RequestParam Integer code) {
        ApiResponse apiResponse = authService.checkCode(code);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }
}
