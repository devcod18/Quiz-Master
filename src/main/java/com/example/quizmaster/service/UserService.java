package com.example.quizmaster.service;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.entity.enums.RoleEnum;
import com.example.quizmaster.payload.ApiResponse;
import com.example.quizmaster.payload.CustomPageable;
import com.example.quizmaster.payload.RegisterRequest;
import com.example.quizmaster.payload.response.ResponseUser;
import com.example.quizmaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final EmailSenderService emailSenderService;

    public ApiResponse saveAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return new ApiResponse("Email allaqachon ishlatilmoqda!", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(RoleEnum.ROLE_ADMIN)
                .enabled(true)
                .activationCode(null)
                .build();

        userRepository.save(user);
        return new ApiResponse("Admin muvaffaqiyatli yaratildi!", HttpStatus.CREATED);
    }

    public ApiResponse getAllAdmins(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> admins = userRepository.findAllByRole(RoleEnum.ROLE_ADMIN, pageRequest);
        List<ResponseUser> responseUsers = toResponseUserList(admins.getContent());

        CustomPageable pageable = CustomPageable.builder()
                .page(admins.getNumber())
                .size(admins.getSize())
                .totalPage(admins.getTotalPages())
                .totalElements(admins.getTotalElements())
                .data(responseUsers)
                .build();

        return new ApiResponse("Barcha Adminlar muvaffaqiyatli olindi!", HttpStatus.OK, pageable);
    }

    public ApiResponse searchUserByFirstName(String name) {
        List<User> users = userRepository.findUsersByFirstName(name);
        return new ApiResponse(toResponseUserList(users));
    }

    public ApiResponse getAllUsers(int size, int page) {
        if (size < 1) size = 1;

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users = userRepository.findAllByRole(RoleEnum.ROLE_USER, pageRequest);
        List<ResponseUser> responseUsers = toResponseUserList(users.getContent());

        CustomPageable pageable = CustomPageable.builder()
                .page(users.getNumber())
                .size(users.getSize())
                .totalPage(users.getTotalPages())
                .totalElements(users.getTotalElements())
                .data(responseUsers)
                .build();

        return new ApiResponse("Barcha Foydalanuvchilar muvaffaqiyatli olindi!", HttpStatus.OK, pageable);
    }

    public ApiResponse getMe(User user) {
        return new ApiResponse("Muvaffaqiyatli", HttpStatus.OK, toResponseUser(user));
    }

    public ApiResponse updateUser(Long userId, RegisterRequest request) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ApiResponse("Foydalanuvchi topilmadi!", HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(user);
        return new ApiResponse("Foydalanuvchi muvaffaqiyatli yangilandi!", HttpStatus.OK);
    }

    public ApiResponse getOne(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ApiResponse("Bu ID bo'yicha foydalanuvchi topilmadi: " + id, HttpStatus.NOT_FOUND);
        }

        return new ApiResponse("Foydalanuvchi topildi!", HttpStatus.OK, toResponseUser(user));
    }

    public ApiResponse sendConfirmationCode(User user) {
        Integer code = authService.generateFiveDigitNumber();
        user.setActivationCode(code);

        userRepository.save(user);
        emailSenderService.sendEmail(user.getEmail(), "Parolni tiklash kodi", String.valueOf(code));

        return new ApiResponse("Tasdiqlash kodi sizning emailingizga yuborildi. Iltimos, tekshirib ko'ring!", HttpStatus.OK);
    }

    public ApiResponse updatePassword(User user, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            return new ApiResponse("Parollar mos kelmaydi!", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new ApiResponse("Parol muvaffaqiyatli yangilandi", HttpStatus.OK);
    }

    public ApiResponse resetPasswordWithCode(Integer code, String newPassword, String confirmPassword) {
        User user = userRepository.findByActivationCode(code);
        if (user == null) {
            return new ApiResponse("No'te tasdiqlash kodi noto'g'ri", HttpStatus.NOT_FOUND);
        }

        user.setActivationCode(null);
        return updatePassword(user, newPassword, confirmPassword);
    }


    private List<ResponseUser> toResponseUserList(List<User> users) {
        return users.stream().map(this::toResponseUser).collect(Collectors.toList());
    }

    private ResponseUser toResponseUser(User user) {
        return ResponseUser.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
