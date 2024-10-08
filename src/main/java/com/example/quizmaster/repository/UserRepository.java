package com.example.quizmaster.repository;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.entity.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User findByActivationCode(Integer code);

    Page<User> findAllByRole(RoleEnum role, PageRequest pageRequest);

    Optional<User> findById(Long id);

    @Query(value = "SELECT * FROM users WHERE first_name ILIKE CONCAT(:name, '%') " +
            "AND (role = 'ROLE_ADMIN' OR role = 'ROLE_SUPER_ADMIN' OR role = 'ROLE_USER')",
            nativeQuery = true)
    List<User> findUsersByFirstName(@Param("name") String name);


    List<User> findAllByEnabledIsFalse();




}
