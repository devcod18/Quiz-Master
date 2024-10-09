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
            return new ApiResponse("Bu email bilan foydalanuvchi allaqachon ro'yxatdan o'tgan!", HttpStatus.BAD_REQUEST, null);
        }

        Integer code = generateFiveDigitNumber();

        String name = extractNameFromEmail(request.email());

        User user = User.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .activationCode(code)
                .role(RoleEnum.ROLE_USER)
                .enabled(false).build();
        userRepository.save(user);

        String emailContent = String.format(
                "Salom %s!\n\n" +
                        "Quiz Master platformasiga xush kelibsiz!\n\n" +
                        "Ro'yxatdan o'tishni tasdiqlash uchun quyidagi kodni kiriting: %d\n\n" +
                        "Bu kodni hech kimga bermang! Agar bu so'rovni siz yubormagan bo'lsangiz, qo'llab-quvvatlash xizmatiga murojaat qiling!\n\n" +
                        "Eng yaxshi tilaklar bilan! Quiz Master jamoasi!"
                , name, code);

        emailSenderService.sendEmail(request.email(), "EMAILINGIZNI TASDIQLANG!", emailContent);

        return new ApiResponse("Foydalanuvchi muvaffaqiyatli ro'yxatdan o'tdi!", HttpStatus.CREATED, null);
    }

    public ApiResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.email());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(request.password(), user.getPassword())) {
                if (user.isEnabled()) {
                    String token = jwtProvider.generateToken(user.getEmail());
                    return new ApiResponse("Kirish muvaffaqiyatli amalga oshirildi", HttpStatus.OK, token);
                }
                return new ApiResponse("Emailingiz hali faollashtirilmagan", HttpStatus.BAD_REQUEST, null);
            }
            return new ApiResponse("Noto'g'ri parol", HttpStatus.BAD_REQUEST, null);
        }

        return new ApiResponse("Email topilmadi", HttpStatus.NOT_FOUND, null);
    }

    public ApiResponse checkCode(Integer code) {
        User userOptional = userRepository.findByActivationCode(code);
        if (userOptional == null) {
            return new ApiResponse("Foydalanuvchi topilmadi", HttpStatus.NOT_FOUND);
        }

        if (userOptional.getActivationCode().equals(code)) {
            userOptional.setActivationCode(null);
            userOptional.setEnabled(true);
            userRepository.save(userOptional);
            return new ApiResponse("Muvaffaqiyatli faollashtirildi", HttpStatus.OK);
        }
        return new ApiResponse("Noto'g'ri faollashtirish kodi", HttpStatus.NOT_FOUND, null);
    }

    private String extractNameFromEmail(String email) {
        String localPart = email.split("@")[0];
        return localPart.substring(0, 1).toUpperCase() + localPart.substring(1);
    }

    public int generateFiveDigitNumber() {
        Random rand = new Random();
        return rand.nextInt(90000) + 10000;
    }
}
