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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // admin saqlash
    public ApiResponse saveAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return new ApiResponse("Email already in use", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(RoleEnum.ROLE_ADMIN)
                .build();

        userRepository.save(user);
        return new ApiResponse("Admin created successfully", HttpStatus.CREATED);
    }

    // barcha adminlar
    public ApiResponse getAllAdmins(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> admins = userRepository.findAllByRole(RoleEnum.ROLE_ADMIN, pageRequest);
        List<ResponseUser> user = toResponseUser(admins.getContent());

        CustomPageable pageable = CustomPageable.builder()
                .page(admins.getNumber())
                .size(admins.getSize())
                .totalPage(admins.getTotalPages())
                .totalElements(admins.getTotalElements())
                .data(user)
                .build();

        return new ApiResponse("Retrieved all Admins", HttpStatus.OK, pageable);
    }

    // user qidiruv
    public ApiResponse searchUserByFirstName(String name) {
        List<User> allUsersSearch = userRepository.findAllUsersSearch(name);
        List<ResponseUser> responseUsers = toResponseUser(allUsersSearch);
        return new ApiResponse(responseUsers);
    }

    // barchha userni olish
    public ApiResponse getAllUsers(int size, int page) {
        if (size < 1) {
            size = 1;  // minimal qiymat
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> admins = userRepository.findAllByRole(RoleEnum.ROLE_USER, pageRequest);
        List<ResponseUser> user = toResponseUser(admins.getContent());

        CustomPageable pageable = CustomPageable.builder()
                .page(admins.getNumber())
                .size(admins.getSize())
                .totalPage(admins.getTotalPages())
                .totalElements(admins.getTotalElements())
                .data(user)
                .build();

        return new ApiResponse("Retrieved all Users", HttpStatus.OK, pageable);
    }

    // profilni korish
    public ApiResponse getMe(User user) {
        ResponseUser responseUser = ResponseUser.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
        return new ApiResponse("Success", HttpStatus.OK, responseUser);
    }

    // userni yangilash
    public ApiResponse updateUser(Long userId, RegisterRequest request) {

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ApiResponse("Foydalanuvchi topilmadi", HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(user);

        return new ApiResponse("User successfully updated", HttpStatus.OK);
    }

    // id boyicha user olish
    public ApiResponse getOne(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ApiResponse("User not found with this id " + id, HttpStatus.NOT_FOUND);
        }

        ResponseUser responseUser = ResponseUser.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();


        return new ApiResponse("User found", HttpStatus.OK, responseUser);
    }

    // userni listga saqlash
    public List<ResponseUser> toResponseUser(List<User> users) {
        List<ResponseUser> users1 = new ArrayList<>();
        for (User admin : users) {
            ResponseUser responseUser = ResponseUser.builder()
                    .email(admin.getEmail())
                    .firstName(admin.getFirstName())
                    .lastName(admin.getLastName())
                    .role(admin.getRole().name())
                    .build();
            users1.add(responseUser);
        }
        return users1;
    }
}
