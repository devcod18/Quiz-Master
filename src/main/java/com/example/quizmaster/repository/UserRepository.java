package com.example.quizmaster.repository;

import com.example.quizmaster.entity.User;
import com.example.quizmaster.entity.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByActivationCode(Integer code);

    Page<User> findAllByRole(RoleEnum role, PageRequest pageRequest);

    Optional<User> findByRole(RoleEnum role);


    @Query(value = "select * from users where first_name ilike CONCAT(:name, '%')", nativeQuery = true)
    List<User> findAllUsersSearch(@Param("name") String name);

    List<User> findAllByEnabledIsFalse();


}
