package com.example.quizmaster.dataloader;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.entity.enums.RoleEnum;
import com.example.quizmaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(ddl.equals("create") || ddl.equals("create-drop")) {
            User user = User.builder()
                    .role(RoleEnum.ROLE_SUPER_ADMIN)
                    .email("sAdmin@gmail.com")
                    .password(passwordEncoder.encode("root123"))
                    .firstName("M")
                    .lastName("M")
                    .enabled(true)
                    .build();
            userRepository.save(user);
        }
    }
}
