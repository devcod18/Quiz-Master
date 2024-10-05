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

    // userni register qilish
    public ApiResponse registerUser(RegisterRequest request) {
        // email formatini tekshirish
        if (!isValidEmail(request.email())) {
            return new ApiResponse("Invalid email format. Only Gmail addresses are allowed!", HttpStatus.BAD_REQUEST, null);
        }

        // foydalanuvchi mavjudligini tekshirish
        boolean userExists = userRepository.existsByEmail(request.email());
        if (userExists) {
            return new ApiResponse("User with this email already exists!", HttpStatus.BAD_REQUEST, null);
        }

        // tasdiqlash kodini generatsiya qilish
        Integer code = generateFiveDigitNumber();

        String name = extractNameFromEmail(request.email());

        // yangi foydalanuvchini yaratish va saqlash
        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .activationCode(code)
                .role(RoleEnum.ROLE_USER)
                .build();
        userRepository.save(user);

        // email mazmuni (o'zgaruvchan format)
        String emailContent = String.format(
                "Hi %s!\n\n" +
                        "We at Quiz Master are excited to welcome you to our platform!\n\n" +
                        "To confirm your registration, please enter the following code: %d\n\n" +
                        "Do not share this code. If you did’t request this, contact support!\n\n" +
                        "Best regards! The Quiz Master Team!"
                , name, code);

        // email yuborish
        emailSenderService.sendEmail(request.email(), "CONFIRM YOUR EMAIL!", emailContent);

        return new ApiResponse("User successfully registered!", HttpStatus.CREATED, null);
    }

    // umimiy login
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

    // aktivatsiya kodini tekshirish
    public ApiResponse checkCode(Integer code) {
        User userOptional = userRepository.findByActivationCode(code);
        if (userOptional == null) {
            return new ApiResponse("User not found", HttpStatus.NOT_FOUND);
        }

        if (userOptional.getActivationCode().equals(code)) {
            userOptional.setActivationCode(null);
            userOptional.setEnabled(true);
            userRepository.save(userOptional);
            return new ApiResponse("Successfully activated", HttpStatus.OK);
        }
        return new ApiResponse("Invalid activation code", HttpStatus.NOT_FOUND, null);
    }

    private String extractNameFromEmail(String email) {
        String localPart = email.split("@")[0];
        return localPart.substring(0, 1).toUpperCase() + localPart.substring(1);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        return email != null && email.matches(emailRegex);
    }

    // activatsiya kodini yaratish
    private int generateFiveDigitNumber() {
        Random rand = new Random();
        return rand.nextInt(90000) + 10000;
    }
}
