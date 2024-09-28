package com.example.quizmaster.service;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.entity.enums.RoleEnum;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.LoginRequest;
import com.example.quizmaster.payload.RegisterRequest;
import com.example.quizmaster.repository.UserRepository;
import com.example.quizmaster.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final EmailSenderService emailSenderService;

    public ApiResponse registerUser(RegisterRequest request) {
        boolean userExists = userRepository.existsByEmail(request.email());
        if (userExists) {
            return new ApiResponse("User with this email already exists", HttpStatus.BAD_REQUEST, null);
        }
        Integer code = generateFiveDigitNumber();
        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .activationCode(code)
                .role(RoleEnum.ROLE_USER)
                .build();
        userRepository.save(user);
        emailSenderService.sendEmail(request.email(), "CONFIRM YOUR EMAIL", "Your activation code: " + code);
        return new ApiResponse("User successfully registered", HttpStatus.CREATED, null);
    }

    public ApiResponse checkCode(Integer code) {
        Optional<User> userOptional = userRepository.findByActivationCode(code);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getActivationCode().equals(code)) {
                user.setEnabled(true);
                user.setActivationCode(null);
                userRepository.save(user);
                return new ApiResponse("Your account has been successfully activated", HttpStatus.OK, null);
            }
        }
        return new ApiResponse("Invalid activation code", HttpStatus.NOT_FOUND, null);
    }

    public ApiResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.email());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(request.password(), user.getPassword())) {
                if (user.isEnabled()) {
                    String token = jwtProvider.generateToken(user.getEmail());
                    return new ApiResponse("Login successful", HttpStatus.OK, token);
                }
                return new ApiResponse("Your email is not activated", HttpStatus.BAD_REQUEST, null);
            }
            return new ApiResponse("Incorrect password", HttpStatus.BAD_REQUEST, null);
        }

        return new ApiResponse("Email not found", HttpStatus.NOT_FOUND, null);
    }

    private int generateFiveDigitNumber() {
        Random rand = new Random();
        return rand.nextInt(90000) + 10000;
    }
}
