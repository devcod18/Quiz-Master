package com.example.quizmaster.scheduled;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 * * * * *")
    public void DeleteEnableUsers() {
        List<User> allByEnabledIsFalse = userRepository.findAllByEnabledIsFalse();
        userRepository.deleteAll(allByEnabledIsFalse);
        log.info("Enabled users deleted: {}", allByEnabledIsFalse.size());
    }

}
