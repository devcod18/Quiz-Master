package com.example.quizmaster.controller;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.RegisterRequest;
import com.example.quizmaster.security.CurrentUser;
import com.example.quizmaster.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Foydalanuvchilarni boshqarish va ularning profiliga oid operatsiyalarni amalga oshiradi.")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Yangi admin yaratish",
            description = "Super admin yangi admin foydalanuvchini yaratishi mumkin")
    @PostMapping("/save-admin")
    public ResponseEntity<ApiResponse> saveAdmin(
            @Valid @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = userService.saveAdmin(request);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Barcha adminlarni olish",
            description = "Barcha admin foydalanuvchilarning sahifalangan ro'yxatini oladi")
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse> getAllAdmins(
            @Valid @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        ApiResponse allAdmins = userService.getAllAdmins(page, size);
        return ResponseEntity.ok(allAdmins);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Foydalanuvchilarni ism bo'yicha qidirish admin uchun",
            description = "Foydalanuvchilarni ularning ismi bo'yicha qidiradi")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @Valid @RequestParam String name,
            @CurrentUser User user) {
        ApiResponse apiResponse = userService.searchUserByFirstName(name);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Foydalanuvchilarni ism bo'yicha qidirish user uchun",
            description = "Foydalanuvchilarni ularning ismi bo'yicha qidiradi")
    @GetMapping("/search/user")
    public ResponseEntity<ApiResponse> searchUser(
            @Valid @RequestParam String name,
            @CurrentUser User user) {
        ApiResponse apiResponse = userService.searchUserByFirstNameUser(name);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Mening profilimni ko'rish",
            description = "Joriy tizimga kirgan foydalanuvchining profilini ko'rsatadi")
    @GetMapping("/get/me")
    public ResponseEntity<ApiResponse> getMe(@Valid @CurrentUser User user) {
        ApiResponse me = userService.getMe(user);
        return ResponseEntity.ok(me);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Barcha foydalanuvchilarni olish",
            description = "Barcha foydalanuvchilarning sahifalangan ro'yxatini oladi")
    @GetMapping("/all/user")
    public ResponseEntity<ApiResponse> getAllUsers(
            @Valid @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        ApiResponse allUsers = userService.getAllUsers(page, size);
        return ResponseEntity.ok(allUsers);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Foydalanuvchi profilini yangilash",
            description = "Foydalanuvchilarga o'z profilini yangilash imkonini beradi")
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateProfile(@Valid @CurrentUser User user,
                                                     @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = userService.updateUser(user, request);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Emailni tasdiqlash",
            description = "Foydalanuvchiga email tasdiqlash kodi yuboradi")
    @PutMapping("/forgotEmail")
    public ResponseEntity<ApiResponse> forgotEmail(@Valid @CurrentUser User user) {
        ApiResponse response = userService.sendConfirmationCode(user);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Parolni yangilash",
            description = "Tasdiqlash kodi orqali foydalanuvchi parolini yangilaydi")
    @PutMapping("/updatePassword")
    public ResponseEntity<ApiResponse> updatePassword(@Valid @RequestParam Integer code,
                                                      @RequestParam String newPassword,
                                                      @RequestParam String confirmPassword) {
        ApiResponse response = userService.resetPasswordWithCode(code, newPassword, confirmPassword);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Foydalanuvchini ID bo'yicha olish",
            description = "Berilgan ID bo'yicha foydalanuvchini qaytaradi.")
    @GetMapping("/getOne/{id}")
    public ResponseEntity<ApiResponse> getOne(@PathVariable Long id) {
        ApiResponse response = userService.getOne(id);
        return ResponseEntity.ok(response);
    }
}
