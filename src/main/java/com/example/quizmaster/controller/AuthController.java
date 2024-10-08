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
@Tag(name = "Auth Controller",
        description = "Foydalanuvchi va adminni ro'yxatdan o'tkazish, kirish va aktivatsiya uchun API lar.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Yangi foydalanuvchini ro'yxatdan o'tkazish",
            description = "Har qanday shaxsga zarur ma'lumotlar bilan yangi foydalanuvchini ro'yxatdan o'tkazish imkonini beradi.")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = authService.registerUser(request);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @PostMapping("/login")
    @Operation(summary = "Foydalanuvchi kirishi",
            description = "Foydalanuvchi o'z elektron pochta manzili va parolini taqdim etib kirish imkonini beradi.")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        ApiResponse apiResponse = authService.login(request);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }

    @PutMapping("/check-code")
    @Operation(summary = "Aktivatsiya kodini tekshirish",
            description = "Foydalanuvchining elektron pochta manziliga yuborilgan aktivatsiya kodini tekshiradi.")
    public ResponseEntity<ApiResponse> checkCode(@Valid @RequestParam Integer code) {
        ApiResponse apiResponse = authService.checkCode(code);
        return new ResponseEntity<>(apiResponse, apiResponse.getCode());
    }
}
