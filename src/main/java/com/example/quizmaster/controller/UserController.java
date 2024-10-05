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
@Tag(name = "User Controller",
        description = "Handles user management operations including admin management and user search")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Create new admin",
            description = "Allows super admin to create a new admin user")
    @PostMapping("/save-admin")
    public ResponseEntity<ApiResponse> saveAdmin(
            @Valid @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = userService.saveAdmin(request);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Fetch all admins",
            description = "Fetches a paginated list of admin users")
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse> getAllAdmins(
            @Valid @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        ApiResponse allAdmins = userService.getAllAdmins(page, size);
        return ResponseEntity.ok(allAdmins);
    }

    @Operation(summary = "Search users by name",
            description = "Searches users by their first name")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> search(
            @Valid @RequestParam String name,
            @CurrentUser User user) {
        ApiResponse apiResponse = userService.searchUserByFirstName(name);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @Operation(summary = "Get my profile",
            description = "Shows the profile of the currently logged-in user")
    @GetMapping("/get/me")
    public ResponseEntity<ApiResponse> getMe(@Valid @CurrentUser User user) {
        ApiResponse me = userService.getMe(user);
        return ResponseEntity.ok(me);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Fetch all users",
            description = "Fetches a paginated list of all users")
    @GetMapping("/all/user")
    public ResponseEntity<ApiResponse> getAllUsers(
            @Valid @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {
        ApiResponse allUsers = userService.getAllUsers(page, size);
        return ResponseEntity.ok(allUsers);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Update user profile",
            description = "Allows users to update their profile")
    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse> updateProfile(@Valid @PathVariable Long userId,
                                                     @RequestBody RegisterRequest request) {
        ApiResponse apiResponse = userService.updateUser(userId, request);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    @Operation(summary = "Get one user by ID",
            description = "Fetches details of a single user by its user ID for users with SUPER_ADMIN or ADMIN roles.")
    @PutMapping("/getOne/{userId}")
    public ResponseEntity<ApiResponse> getOne(@Valid @PathVariable Long userId) {
        ApiResponse one = userService.getOne(userId);
        return ResponseEntity.ok(one);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Confirm user email",
            description = "Sends a confirmation code to the specified email for verification.")
    @PutMapping("/confirmEmail")
    public ResponseEntity<ApiResponse> confirmEmail(@Valid @RequestParam String email) {
        ApiResponse response = userService.sendConfirmationCode(email);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Update password",
            description = "Allows the user to reset their password using a confirmation code.")
    @PutMapping("/updatePassword")
    public ResponseEntity<ApiResponse> updatePassword(@Valid @RequestParam Integer code,
                                                      @RequestParam String newPassword,
                                                      @RequestParam String confirmPassword) {
        ApiResponse response = userService.resetPasswordWithCode(code, newPassword, confirmPassword);
        return ResponseEntity.ok(response);
    }

}
