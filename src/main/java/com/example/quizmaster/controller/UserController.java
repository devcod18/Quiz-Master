package com.example.quizmaster.controller;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.RegisterRequest;
import com.example.quizmaster.security.CurrentUser;
import com.example.quizmaster.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User Controller",
        description = "Handles user management operations including admin management and user search")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Save a new admin user", description = "Allows a super admin to create a new admin user")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("/save-admin")
    public ResponseEntity<ApiResponse> saveAdmin(
            @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = userService.saveAdmin(request);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @Operation(summary = "Get all admins", description = "Fetches a paginated list of all admin users")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse> getAllAdmins(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        ApiResponse allAdmins = userService.getAllAdmins(page, size);
        return ResponseEntity.ok(allAdmins);
    }

    @Operation(summary = "Search for users", description = "Searches users by first name")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @RequestParam String name,
            @CurrentUser User user) {
        ApiResponse apiResponse = userService.searchUserByFirstName(name);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Userni uzini profilini kurish")
    @GetMapping("/get/me")
    public ResponseEntity<ApiResponse> getMe(@CurrentUser User user){
        ApiResponse me = userService.getMe(user);
        return ResponseEntity.ok(me);
    }
}
